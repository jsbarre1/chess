package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    final private Map<String, GameData> gameList;

    public MemoryGameDAO() {
        this.gameList = new HashMap<>();
    }

    public void deleteAllGames(){
        gameList.clear();
    }

    public Map<String, GameData> getGames(){
        return gameList;
    }
}
