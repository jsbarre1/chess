package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    final private Collection<AuthData> gameList = new ArrayList<>();

    public void deleteAllGames(){
        gameList.clear();
    }
}
