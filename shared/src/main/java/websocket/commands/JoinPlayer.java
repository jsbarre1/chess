package websocket.commands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
  public ChessGame.TeamColor playerColor;
  public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor, String username) {
    super(CommandType.CONNECT_PLAYER, authToken, gameID, username);
    this.playerColor = playerColor;

  }
}
