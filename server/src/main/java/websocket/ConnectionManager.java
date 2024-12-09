package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  public void add(String userName, Session session, String authToken) {
    var connection = new Connection(userName, session, authToken);
    connections.put(userName, connection);
  }


  public void remove(String username) {
    connections.remove(username);
  }

  public void broadcast(String username, ServerMessage notification) throws IOException {
    if (notification == null) {
      throw new IOException("Cannot broadcast null message");
    }

    var removeList=new ArrayList<Connection>();
    for (var c : connections.values()) {
      try {
        if (c.session.isOpen()) {
          if (!c.username.equals(username)) {
            c.send(notification);
          }
        } else {
          removeList.add(c);
        }
      } catch (Exception e) {
        removeList.add(c);
        System.err.println("Error broadcasting to user: " + c.username + " - " + e.getMessage());
      }
    }

    // Clean up closed connections
    for (var c : removeList) {
      connections.remove(c.username);
    }
  }
}