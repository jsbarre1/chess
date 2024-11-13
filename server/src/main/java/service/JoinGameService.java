package service;

import dataaccess.*;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.AuthData;
import model.GameData;
import models.JoinGameObject;

import java.util.Map;

public class JoinGameService {
    private final AuthFunctions authFunctions;
    private final GameFunctions gameFunctions;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO){
        this.authFunctions = new AuthFunctions(authDAO);
        this.gameFunctions = new GameFunctions(gameDAO);
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
