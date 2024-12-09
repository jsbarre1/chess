package websocket;

import websocketnotifications.Notification;

public interface NotificationHandler {
  void notify(Notification notification);
}
