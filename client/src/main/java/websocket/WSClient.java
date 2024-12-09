package websocket;

import com.google.gson.Gson;
import exceptions.ResponseException;
import websocketnotifications.Notification;

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
          Notification notification=new Gson().fromJson(message, Notification.class);
          notificationHandler.notify(notification);
        }
      });
    } catch (DeploymentException | URISyntaxException | IOException ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {

  }
}
