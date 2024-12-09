package websocket.messages;

public class ErrorMessage extends ServerMessage{

  public ErrorMessage(String errorMessage) {
    this.message = errorMessage;
    setMessageType(ServerMessageType.ERROR);
  }

}
