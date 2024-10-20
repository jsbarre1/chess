package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;

import java.util.HashMap;

public class CreateGameService {
    AuthFunctions authFunctions;
    GameFunctions gameFunctions;

    public CreateGameService(MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
        this.gameFunctions = new GameFunctions(memoryGameDAO);
    }

    public Integer createGame(String authToken) throws ResponseException{
        if(!authFunctions.isAuthenticated(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }else{
            return 1;
        }
    }
}
