package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.Map;

public interface GameDAO {
    void deleteAllGames() throws DataAccessException;
    Map<String, GameData> getGames() throws DataAccessException;
}
