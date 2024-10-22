package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.AuthData;
import model.GameData;
import models.JoinGameObject;

import java.util.Map;

public class JoinGameService {
    private AuthFunctions authFunctions;
    private GameFunctions gameFunctions;

    public JoinGameService(MemoryGameDAO memoryGameDAO, MemoryAuthDAO memoryAuthDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
        this.gameFunctions = new GameFunctions(memoryGameDAO);
    }

    public Map<Integer, GameData> joinGame(String authToken, JoinGameObject colorAndId) throws ResponseException, DataAccessException {
        if(colorAndId.gameID() == null || colorAndId.playerColor() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        authFunctions.checkAuth(authToken);
        AuthData currAuth = authFunctions.getAuth(authToken);
        GameData oldGame = gameFunctions.getGame(colorAndId.gameID());
        gameFunctions.alreadyJoined(oldGame, colorAndId);
        return gameFunctions.joinGame(oldGame, colorAndId.playerColor(), currAuth.username());
    }
}
