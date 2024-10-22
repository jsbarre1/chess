package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.GameData;

import java.util.ArrayList;

public class ListGamesService {
    private GameFunctions gameFunctions;
    private AuthFunctions authFunctions;

    public ListGamesService(MemoryGameDAO memoryGameDAO, MemoryAuthDAO memoryAuthDAO){
        this.gameFunctions = new GameFunctions(memoryGameDAO);
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException, DataAccessException {
        authFunctions.checkAuth(authToken);
        return gameFunctions.getGamesArray();
    }
}
