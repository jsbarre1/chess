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
    var removeList = new ArrayList<Connection>();
    for (var c : connections.values()) {
      if (c.session.isOpen()) {
        if (!c.username.equals(username)) {
          c.send(notification);
        }
      } else {
        removeList.add(c);
      }
    }


    // Clean up any connections that were left open.
    for (var c : removeList) {
      connections.remove(c.username);
    }
  }
}