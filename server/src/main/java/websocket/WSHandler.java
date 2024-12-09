package websocket;


import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.Connect;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WSHandler {
  private GameDAO gameDAO;
  private AuthDAO authDAO;
  private final ConnectionManager connections=new ConnectionManager();
  private final Gson gson=new Gson();
  private String currentUsername;
  private GameData currGame;

  public WSHandler() {
  }

  public WSHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
    this.authDAO=authDAO;
    this.gameDAO=gameDAO;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) {
    try {
      UserGameCommand userGameCommand=gson.fromJson(message, UserGameCommand.class);
      switch (userGameCommand.getCommandType()) {
        case CONNECT -> connect(session, new Gson().fromJson(message, Connect.class));
        case MAKE_MOVE -> makeGameMove(session, new Gson().fromJson(message, MakeMove.class));
        case RESIGN -> resign(session, new Gson().fromJson(message, Resign.class));
        case LEAVE -> handleLeave(session);
      }
    } catch (Exception e) {
      try {
        ErrorMessage error=new ErrorMessage("Error: " + e.getMessage());
        session.getRemote().sendString(gson.toJson(error));
      } catch (IOException ex) {
        System.err.println("Failed to send error message: " + ex.getMessage());
      }
    }
  }

  private void resign(Session session, Resign resignCommand) throws IOException {
    try {
      AuthData auth = authDAO.getAuth(resignCommand.getAuthToken());
      currGame =gameDAO.getGame(resignCommand.getGameID());


      ChessGame.TeamColor playerColor=null;


      if (Objects.equals(currGame.whiteUsername(), auth.username())) {
        playerColor=ChessGame.TeamColor.WHITE;
      } else if (Objects.equals(currGame.blackUsername(), auth.username())) {
        playerColor=ChessGame.TeamColor.BLACK;
      }

      String opUsername;
      if (playerColor == ChessGame.TeamColor.WHITE) {
        opUsername = currGame.blackUsername();
      } else {
        opUsername = currGame.whiteUsername();
      }

      if (currGame.game().isGameOver()) {
        sendError(session, "Error: You cannot resign, the game is over already");
        return;
      }

      if (playerColor == null) {
        sendError(session, "Error: You are observing this game");
        return;
      }

      currGame.game().setGameOver(true);
      gameDAO.setGame(currGame.gameID(), currGame);
      NotificationMessage notification = new NotificationMessage("%s has forfeited, %s wins!".formatted(auth.username(), opUsername));
      connections.broadcast("", notification);
    } catch (DataAccessException e) {
      sendError(session, "Error: Not authorized");
    } catch (ResponseException e) {
      sendError(session, "Error: invalid game");
    }

  }


  private void connect(Session session, Connect connect) throws IOException {
    try {
      if (connect == null) {
        sendError(session, "Error: invalid connection request");
        return;
      }

      System.out.println("Connect request received for game: " + connect.getGameID());

      int gameID=connect.getGameID();
      var gameData=gameDAO.getGame(gameID);
      currGame = gameData;

      if (gameData == null) {
        sendError(session, "Error: game not found");
        return;
      }

      String authToken=connect.getAuthToken();
      var authData=authDAO.getAuth(authToken);
      if (authData == null) {
        sendError(session, "Error: unauthorized");
        return;
      }

      System.out.println("Creating notification for user: " + authData.username());

      currentUsername=authData.username();
      ChessGame chessGame=gameData.game();
      ChessGame.TeamColor teamColor=connect.getColor();

      LoadGameMessage loadGameMessage=new LoadGameMessage(chessGame);
      if (loadGameMessage.getGame() == null) {
        sendError(session, "Error: invalid game state");
        return;
      }

      System.out.println("Sending load game message: " + gson.toJson(loadGameMessage));
      session.getRemote().sendString(gson.toJson(loadGameMessage));

      String teamColorStr=(teamColor != null) ? " as " + teamColor : " as an observer";
      NotificationMessage notification=new NotificationMessage(currentUsername + " joined the game" + teamColorStr);

      System.out.println("Sending notification: " + gson.toJson(notification));

      connections.add(currentUsername, session, authToken);

      try {
        connections.broadcast(currentUsername, notification);
      } catch (IOException e) {
        System.err.println("Failed to broadcast notification: " + e.getMessage());
      }

    } catch (DataAccessException e) {
      System.err.println("Database error: " + e.getMessage());
      sendError(session, "Error: database access failed");
    } catch (ResponseException e) {
      System.err.println("Response error: " + e.getMessage());
      sendError(session, "Error: " + e.getMessage());
    }
  }

  private void makeGameMove(Session session, MakeMove moveCommand) throws Exception {

    var authData=authDAO.getAuth(moveCommand.getAuthToken());
    String currUsername=authData.username();
    Connection playerConnection=new Connection(currUsername, session, moveCommand.getAuthToken());
    ChessMove move=moveCommand.getMove();

    var gameData=gameDAO.getGame(moveCommand.getGameID());
    currGame=gameData;
    ChessGame.TeamColor currColor=null;
    ChessGame.TeamColor opponentColor=null;


    if (Objects.equals(gameData.whiteUsername(), currUsername)) {
      currColor=ChessGame.TeamColor.WHITE;
      opponentColor=ChessGame.TeamColor.BLACK;
    } else if (Objects.equals(gameData.blackUsername(), currUsername)) {
      currColor=ChessGame.TeamColor.BLACK;
      opponentColor=ChessGame.TeamColor.WHITE;
    }

    if (!validateMove(moveCommand, session, currColor)) {
      return;
    }
    try {
      gameData.game().makeMove(move);
      updateGameState(moveCommand.getGameID(), move, currUsername, opponentColor);

    } catch (InvalidMoveException e) {
      handleInvalidMove(playerConnection);
    }
  }

  private Boolean validateMove(MakeMove moveCommand, Session session, ChessGame.TeamColor currColor) throws ResponseException, IOException {
    ChessPiece piece=currGame.game().getBoard().getPiece(moveCommand.getMove().getStartPosition());
    if (currGame.game().getBoard().getPiece(moveCommand.getMove().getStartPosition()).getTeamColor() != currColor) {
      sendError(session, "You can't move other team's pieces");
      return false;
    }
    if (currGame.game().getTeamTurn() != currColor) {
      sendError(session, "Please wait until your turn");
      return false;
    }

    if (piece == null) {
      sendError(session, "No piece at this location");
      return false;
    }

    if (currColor == null) {
      sendError(session, "You are only an observer");
      return false;
    }

    if (currGame.game().isGameOver()) {
      sendError(session, "Game Over. No moves allowed");
      return false;
    }
    currGame.game().setTeamTurn(piece.getTeamColor());
    return true;
  }

  private void updateGameState(int gameId, ChessMove move, String currUsername, ChessGame.TeamColor opponentColor ) throws DataAccessException, IOException {
    ChessPiece movedPiece=currGame.game().getBoard().getPiece(move.getEndPosition());
    ChessPosition endPos=move.getEndPosition();

    String moveDescription=String.format("%s has moved %s to %d,%d",
            currUsername,
            movedPiece.getPieceType(),
            endPos.getRow(),
            endPos.getColumn());

    NotificationMessage n;

    if (currGame.game().isInCheckmate(opponentColor)) {
      n = new NotificationMessage("Checkmate! %s wins!".formatted(currUsername));
      currGame.game().setGameOver(true);
    } else if (currGame.game().isInStalemate(opponentColor)) {
      n = new NotificationMessage("Stalemate caused by %s's move! It's a tie!".formatted(currUsername));
      currGame.game().setGameOver(true);
    } else if (currGame.game().isInCheck(opponentColor)) {
      n = new NotificationMessage("%s made a move. %s is now in check!".formatted(currUsername, opponentColor));
    } else {
      n = new NotificationMessage(moveDescription);
    }

    ServerMessage notification= n;
    ServerMessage loadMessage=new LoadGameMessage(currGame.game());

    gameDAO.setGame(gameId, currGame);
    connections.broadcast("", loadMessage);
    connections.broadcast(currUsername, notification);
  }

  private void handleInvalidMove(Connection playerConnection) throws IOException {
    String errorMsg="It's not your turn, please wait for the other player.";
    ServerMessage error=new ErrorMessage(errorMsg);
    playerConnection.send(error);
  }

  private void handleLeave(Session session) {
    connections.remove(currentUsername);
  }

  private void sendError(Session session, String message) throws IOException {
    ErrorMessage error=new ErrorMessage(message);
    System.out.println("Sending error message: " + gson.toJson(error));
    session.getRemote().sendString(gson.toJson(error));
  }
}