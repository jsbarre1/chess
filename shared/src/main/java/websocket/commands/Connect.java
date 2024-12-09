package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand{

  private final ChessGame.TeamColor color;


  public Connect(String authToken, int gameID, ChessGame.TeamColor color) {
    super(CommandType.CONNECT, authToken, gameID);
    this.commandType = CommandType.CONNECT;
    this.color = color;
  }

  public Integer getGameID() {
    return super.getGameID();
  }

  public ChessGame.TeamColor getColor() {
    return color;
  }
}
