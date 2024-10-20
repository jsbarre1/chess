package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ListGamesService {
    MemoryGameDAO memoryGameDAO;
    AuthFunctions authFunctions;

    public ListGamesService(MemoryGameDAO memoryGameDAO, MemoryAuthDAO memoryAuthDAO){
        this.memoryGameDAO = memoryGameDAO;
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
    }

    public Map<String, GameData> listGames(String authToken) throws ResponseException{
        if(!authFunctions.isAuthenticated(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }else{
            return memoryGameDAO.getGames();
        }
    }
}
