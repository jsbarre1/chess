package websocket.messages;

public class NotificaitonMessage extends ServerMessage {

  public NotificaitonMessage(String notification) {
    this.message=notification;
    setMessageType(ServerMessageType.NOTIFICATION);
  }

}
