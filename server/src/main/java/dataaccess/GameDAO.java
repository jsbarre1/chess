package dataaccess;

import chess.ChessGame;
import exceptions.ResponseException;
import model.GameData;

import java.util.ArrayList;
import java.util.Map;

public interface GameDAO {
    void deleteAllGames() throws DataAccessException, ResponseException;
    Map<Integer, GameData> getGames() throws DataAccessException, ResponseException;
    ArrayList<GameData> getGamesArray() throws DataAccessException, ResponseException;
    GameData addGame(GameData gameData) throws DataAccessException;
    GameData getGame(Integer id) throws DataAccessException, ResponseException;
    public Map<Integer, GameData> addPlayerToGame(GameData oldGame, String playerColor, String username)throws DataAccessException;
    void setGame(Integer gameID, ChessGame game) throws DataAccessException;

}
