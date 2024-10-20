package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    final private Map<Integer, GameData> gameList;

    public MemoryGameDAO() {
        this.gameList = new HashMap<>();
    }

    public void deleteAllGames(){
        gameList.clear();
    }

    public Map<Integer, GameData> getGames(){
        return gameList;
    }

    public Integer addGame(GameData gameData){
        gameList.put(gameData.gameID(), gameData);
        return gameData.gameID();
    }
}
