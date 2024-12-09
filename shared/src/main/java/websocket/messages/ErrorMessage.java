package websocket.messages;

public class ErrorMessage extends ServerMessage{
  private String errorMessage;

  public ErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    setMessageType(ServerMessageType.ERROR);
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
