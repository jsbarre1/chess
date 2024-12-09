package websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import exceptions.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.Connect;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificaitonMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WSHandler {

  private UserDAO userDAO;
  private GameDAO gameDAO;
  private AuthDAO authDAO;


  private final ConnectionManager connections = new ConnectionManager();

  public WSHandler(){}

  public WSHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
    this.userDAO = userDAO;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException, ResponseException {
    UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
    switch (userGameCommand.getCommandType()) {
      case CONNECT -> connect(session, message);
      case LEAVE -> {
        return;
      }
    }
  }

  private void connect(Session session, String message) throws IOException, ResponseException, DataAccessException {
      Connect connect = new Gson().fromJson(message, Connect.class);

      int gameID = connect.getGameID();
      var gameData = gameDAO.getGame(gameID);
      if (gameData == null) {
        ErrorMessage invalidGame = new ErrorMessage("Error: game not found");
        session.getRemote().sendString(new Gson().toJson(invalidGame));
        return;
      }

      String authToken = connect.getAuthToken();

      var authData = authDAO.getAuth(authToken);
      if (authData == null) {
        ErrorMessage notAuthorized = new ErrorMessage("Error: unauthorized");
        session.getRemote().sendString(new Gson().toJson(notAuthorized));
        return;
      }

      String username = authData.username();
      ChessGame chessGame = gameData.game();
      ChessGame.TeamColor teamColor = connect.getColor();

      session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(chessGame)));

      String teamColorStr = (teamColor != null) ? " as " + teamColor.toString() : " as an observer";
      ServerMessage serverMessage = new NotificaitonMessage(username + " joined the game" + teamColorStr);

      connections.add(username, session, authToken);
      connections.broadcast(username, serverMessage);

  }
}

