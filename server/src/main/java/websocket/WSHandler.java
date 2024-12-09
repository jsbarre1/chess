package websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import exceptions.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.Connect;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WSHandler {
  private UserDAO userDAO;
  private GameDAO gameDAO;
  private AuthDAO authDAO;
  private final ConnectionManager connections = new ConnectionManager();
  private final Gson gson = new Gson();
  private String currentUsername;

  public WSHandler() {}

  public WSHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
    this.userDAO = userDAO;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) {
    try {
      UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);
      switch (userGameCommand.getCommandType()) {
        case CONNECT -> connect(session, new Gson().fromJson(message, Connect.class));
        case LEAVE -> handleLeave(session);
      }
    } catch (Exception e) {
      try {
        ErrorMessage error = new ErrorMessage("Error: " + e.getMessage());
        session.getRemote().sendString(gson.toJson(error));
      } catch (IOException ex) {
        System.err.println("Failed to send error message: " + ex.getMessage());
      }
    }
  }

  private void connect(Session session, Connect connect) throws IOException {
    try {
      if (connect == null) {
        sendError(session, "Error: invalid connection request");
        return;
      }

      System.out.println("Connect request received for game: " + connect.getGameID());

      int gameID = connect.getGameID();
      var gameData = gameDAO.getGame(gameID);
      if (gameData == null) {
        sendError(session, "Error: game not found");
        return;
      }

      String authToken = connect.getAuthToken();
      var authData = authDAO.getAuth(authToken);
      if (authData == null) {
        sendError(session, "Error: unauthorized");
        return;
      }

      System.out.println("Creating notification for user: " + authData.username());

      currentUsername = authData.username();
      ChessGame chessGame = gameData.game();
      ChessGame.TeamColor teamColor = connect.getColor();

      LoadGameMessage loadGameMessage = new LoadGameMessage(chessGame);
      if (loadGameMessage.getGame() == null) {
        sendError(session, "Error: invalid game state");
        return;
      }

      System.out.println("Sending load game message: " + gson.toJson(loadGameMessage));
      session.getRemote().sendString(gson.toJson(loadGameMessage));

      String teamColorStr = (teamColor != null) ? " as " + teamColor : " as an observer";
      NotificationMessage notification = new NotificationMessage(currentUsername + " joined the game" + teamColorStr);

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

  private void handleLeave(Session session) {
    connections.remove(currentUsername);
  }

  private void sendError(Session session, String message) throws IOException {
    ErrorMessage error = new ErrorMessage(message);
    System.out.println("Sending error message: " + gson.toJson(error));
    session.getRemote().sendString(gson.toJson(error));
  }
}