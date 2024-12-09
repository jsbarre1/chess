package websocket.messages;

public class NotificationMessage extends ServerMessage {

  public NotificationMessage(String notification) {
    super(ServerMessageType.NOTIFICATION); 
    this.message = notification;
  }

}
