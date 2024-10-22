package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Map;

public interface GameDAO {
    void deleteAllGames() throws DataAccessException;
    Map<Integer, GameData> getGames() throws DataAccessException;
    ArrayList<GameData> getGamesArray() throws DataAccessException;
    GameData addGame(GameData gameData) throws DataAccessException;
    GameData getGame(Integer id) throws DataAccessException;
    public Map<Integer, GameData> addPlayerToGame(GameData oldGame, String playerColor, String username)throws DataAccessException;
}
