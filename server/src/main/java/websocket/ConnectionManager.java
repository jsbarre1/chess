package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

  private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<Integer, ArrayList<String>> gameConnections = new ConcurrentHashMap<>();

  public void add(String username, Session session, String authToken, int gameId) {
    var connection = new Connection(username, session, authToken);
    connections.put(username, connection);

    gameConnections.computeIfAbsent(gameId, k -> new ArrayList<>()).add(username);
  }

  public void remove(String username) {
    connections.remove(username);
    gameConnections.values().forEach(users -> users.remove(username));
  }

  public void broadcast(String username, ServerMessage notification, int gameId) throws IOException {
    if (notification == null) {
      throw new IOException("Cannot broadcast null message");
    }

    var removeList = new ArrayList<Connection>();
    var gameUsers = gameConnections.get(gameId);
    if (gameUsers == null) {
      return;
    }

    for (String gameUser : gameUsers) {
      var connection = connections.get(gameUser);
      if (connection == null) {
        continue;
      }

      if (!connection.session.isOpen()) {
        removeList.add(connection);
        continue;
      }

      try {
        if (!connection.username.equals(username)) {
          connection.send(notification);
        }
      } catch (Exception e) {
        removeList.add(connection);
        System.err.println("Error broadcasting to user: " + connection.username + " - " + e.getMessage());
      }
    }

    for (var c : removeList) {
      connections.remove(c.username);
    }
  }

}