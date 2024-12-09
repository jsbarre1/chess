package websocket.messages;

public class NotificationMessage extends ServerMessage {

  public NotificationMessage(String notification) {
    this.message=notification;
    setMessageType(ServerMessageType.NOTIFICATION);
  }

}
