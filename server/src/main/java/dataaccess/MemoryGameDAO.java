package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    final private Map<Integer, GameData> gameList;

    public MemoryGameDAO() {
        this.gameList = new HashMap<>();
    }

    public void deleteAllGames()throws DataAccessException{
        try{
            gameList.clear();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Map<Integer, GameData> getGames()throws DataAccessException{
        try{
            return gameList;
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData addGame(GameData gameData) throws DataAccessException {
        try {
            gameList.put(gameData.gameID(), gameData);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return new GameData(gameData.gameID(), null, null, null, null);
    }

    public GameData getGame(Integer id) throws DataAccessException{
        GameData targetGame = gameList.get(id);

        if(targetGame == null){
            throw new DataAccessException("Game Not Found");
        }

        else return targetGame;
    }
}
