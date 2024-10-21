package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.GameData;

public class CreateGameService {
    AuthFunctions authFunctions;
    GameFunctions gameFunctions;

    public CreateGameService(MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
        this.gameFunctions = new GameFunctions(memoryGameDAO);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException, DataAccessException {
        if(authFunctions.isNotAuthenticated(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }else{
           return gameFunctions.createGame(gameName);
        }
    }
}
