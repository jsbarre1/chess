package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;

public class CreateGameService {
    AuthFunctions authFunctions;
    GameFunctions gameFunctions;

    public CreateGameService(MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
        this.gameFunctions = new GameFunctions(memoryGameDAO);
    }

    public Object createGame(String authToken, String gameName) throws ResponseException{
        if(authFunctions.isNotAuthenticated(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }else{
           return gameFunctions.createGame(gameName);
        }
    }
}
