package service;

import dataaccess.*;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.GameData;

public class CreateGameService {
    private final AuthFunctions authFunctions;
    private final GameFunctions gameFunctions;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authFunctions = new AuthFunctions(authDAO);
        this.gameFunctions = new GameFunctions(gameDAO);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException, DataAccessException {
        if(gameName == null){
            throw new ResponseException(400, "Error: bad request");
        }
        authFunctions.checkAuth(authToken);
        return gameFunctions.createGame(gameName);
    }
}
