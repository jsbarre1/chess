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
        case CONNECT -> connect(session, message);
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

  private void connect(Session session, String message) throws IOException {
    try {
      Connect connect = gson.fromJson(message, Connect.class);

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

      currentUsername= authData.username();
      ChessGame chessGame = gameData.game();
      ChessGame.TeamColor teamColor = connect.getColor();

      // Send game state
      LoadGameMessage loadGameMessage = new LoadGameMessage(chessGame);
      session.getRemote().sendString(gson.toJson(loadGameMessage));

      // Send notification
      String teamColorStr = (teamColor != null) ? " as " + teamColor.toString() : " as an observer";
      NotificationMessage notification= new NotificationMessage(currentUsername + " joined the game" + teamColorStr);

      connections.add(currentUsername, session, authToken);
      connections.broadcast(currentUsername, notification);

    } catch (DataAccessException e) {
      sendError(session, "Error: database access failed");
    } catch (ResponseException e) {
      sendError(session, "Error: " + e.getMessage());
    }
  }

  private void handleLeave(Session session) {
    connections.remove(currentUsername);
  }

  private void sendError(Session session, String message) throws IOException {
    ErrorMessage error = new ErrorMessage(message);
    session.getRemote().sendString(gson.toJson(error));
  }
}