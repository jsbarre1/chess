package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
public class Connection {
  public String username;
  public Session session;
  private String authToken;
  private final Gson gson = new Gson();  // Single Gson instance

  public Connection(String username, Session session, String authToken) {
    this.username = username;
    this.session = session;
    this.authToken = authToken;
  }

  public void send(ServerMessage msg) throws IOException {
    if (msg == null) {
      throw new IOException("Cannot send null message");
    }
    String jsonMessage = gson.toJson(msg);
    if (session.isOpen()) {
      session.getRemote().sendString(jsonMessage);
    }
  }

}