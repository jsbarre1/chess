package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
  private final ChessGame chessGame;

  public LoadGameMessage(ChessGame chessGame) {
    this.chessGame= chessGame;
    setMessageType(ServerMessageType.LOAD_GAME);
  }

  public ChessGame getGame() {
    return chessGame;
  }
}
