package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.GameData;

public class CreateGameService {
    private AuthFunctions authFunctions;
    private GameFunctions gameFunctions;

    public CreateGameService(MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
        this.gameFunctions = new GameFunctions(memoryGameDAO);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException, DataAccessException {
        if(gameName == null){
            throw new ResponseException(400, "Error: bad request");
        }
        authFunctions.checkAuth(authToken);
        return gameFunctions.createGame(gameName);
    }
}
