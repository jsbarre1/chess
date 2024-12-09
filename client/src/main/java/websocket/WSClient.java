package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.JoinPlayer;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificaitonMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;

public class WSClient extends Endpoint {
  Session session;
  NotificationHandler notificationHandler;

  public WSClient(String url, NotificationHandler notificationHandler) throws ResponseException {
    try {
      url=url.replace("http", "ws");
      URI socketURI=new URI(url + "/ws");
      this.notificationHandler=notificationHandler;

      WebSocketContainer container=ContainerProvider.getWebSocketContainer();
      this.session=container.connectToServer(this, socketURI);

      //set message handler
      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
          switch(serverMessage.getServerMessageType())
          {
            case NOTIFICATION -> {
              NotificaitonMessage notification = new Gson().fromJson(message, NotificaitonMessage.class);
              notificationHandler.notify(notification);
            }
            case LOAD_GAME -> {
              LoadGameMessage notification = new Gson().fromJson(message, LoadGameMessage.class);
              notificationHandler.updateGame(notification);
            }
            case ERROR -> {
              ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
              notificationHandler.notify(error);
            }
          }

          ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
          notificationHandler.notify(notification);
        }
      });
    } catch (DeploymentException | URISyntaxException | IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  public void joinPlayer(int gameID, String authToken, String username, ChessGame.TeamColor teamColor) throws ResponseException {
    try {
      UserGameCommand joinPlayerCommand = new JoinPlayer(authToken, gameID, teamColor, username);
      this.session.getBasicRemote().sendText(new Gson().toJson(joinPlayerCommand));
    }
    catch (IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }


  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {

  }
}
