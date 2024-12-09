package websocket.messages;

public class NotificaitonMessage extends ServerMessage {
  private String message;

  public NotificaitonMessage(String notification) {
    message=notification;
    setMessageType(ServerMessageType.NOTIFICATION);
  }

  public String getMessage() {
    return message;
  }

}
