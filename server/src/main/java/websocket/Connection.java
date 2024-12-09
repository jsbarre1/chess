package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
  public String username;
  public Session session;
  private String authToken;

  public Connection(String username, Session session, String authToken) {
    this.username= username;
    this.session = session;
    this.authToken = authToken;
  }

  public void send(String msg) throws IOException {
    session.getRemote().sendString(msg);
  }
  public void send(ServerMessage msg) throws IOException {
    send(new Gson().toJson(msg));
  }

}