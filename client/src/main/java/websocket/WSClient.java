package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;

public class WSClient extends Endpoint {
  private volatile Session session;
  private final NotificationHandler notificationHandler;
  private final String serverUrl;

  public WSClient(String url, NotificationHandler notificationHandler) throws ResponseException {
    this.notificationHandler=notificationHandler;
    this.serverUrl=url.replace("http", "ws") + "/ws";
    connectToServer();
  }

  private void connectToServer() throws ResponseException {
    try {
      URI socketURI=new URI(serverUrl);
      WebSocketContainer container=ContainerProvider.getWebSocketContainer();
      this.session=container.connectToServer(this, socketURI);

      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          ServerMessage serverMessage=new Gson().fromJson(message, ServerMessage.class);
          switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> {
              NotificationMessage notification=new Gson().fromJson(message, NotificationMessage.class);
              notificationHandler.notify(notification);
            }
            case LOAD_GAME -> {
              LoadGameMessage loadGame=new Gson().fromJson(message, LoadGameMessage.class);
              notificationHandler.updateGame(loadGame);
            }
            case ERROR -> {
              ErrorMessage error=new Gson().fromJson(message, ErrorMessage.class);
              notificationHandler.notify(error);
            }
          }
        }
      });
    } catch (DeploymentException | URISyntaxException | IOException ex) {
      throw new ResponseException(500, "Failed to connect to WebSocket server: " + ex.getMessage());
    }
  }

  public void joinPlayer(int gameID, String authToken, ChessGame.TeamColor teamColor) throws ResponseException {
    try {
      if (session == null || !session.isOpen()) {
        throw new ResponseException(401, "WebSocket session is not connected");
      }
      UserGameCommand joinPlayerCommand=new Connect(authToken, gameID, teamColor);
      this.session.getBasicRemote().sendText(new Gson().toJson(joinPlayerCommand));
    } catch (IOException ex) {
      throw new ResponseException(500, "Failed to send join player command: " + ex.getMessage());
    }
  }

  public void joinObserver(int gameID, String authToken, ChessGame.TeamColor teamColor) throws ResponseException {
    try {
      UserGameCommand joinObserverCommand=new Connect(authToken, gameID, teamColor);
      this.session.getBasicRemote().sendText(new Gson().toJson(joinObserverCommand));
    } catch (IOException ex) {
      throw new ResponseException(500, "Failed to send join observer command: " + ex.getMessage());
    }
  }

  public void resign(int gameID, String authToken) throws ResponseException {
    UserGameCommand resignCommand=new Resign(authToken, gameID);
    String message=new Gson().toJson(resignCommand);
    sendMessage(message);
  }

  public void leave(int gameID, String authToken) throws ResponseException {
    UserGameCommand leaveCommand = new Leave(authToken, gameID);
    String message = new Gson().toJson(leaveCommand);
    sendMessage(message);
  }


  public void makeMove(int gameID, String authToken, ChessMove move) throws ResponseException {
    UserGameCommand makeMoveCommand=new MakeMove(authToken, gameID, move);
    String message=new Gson().toJson(makeMoveCommand);
    sendMessage(message);
  }

  public void sendMessage(String message) throws ResponseException {
    if (session == null || !session.isOpen()) {
      throw new ResponseException(401, "WebSocket session is not connected");
    }
    try {
      this.session.getBasicRemote().sendText(message);
    } catch (IOException ex) {
      throw new ResponseException(500, "Failed to send message: " + ex.getMessage());
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    if (this.session == null) {
      this.session=session;
    }
  }

  @Override
  public void onClose(Session session, CloseReason closeReason) {
    this.session=null;
  }

  @Override
  public void onError(Session session, Throwable throwable) {
    this.session=null;
    notificationHandler.notify(new ErrorMessage("WebSocket error: " + throwable.getMessage()));
  }
}