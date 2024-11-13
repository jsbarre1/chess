package service;

import dataaccess.*;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.GameFunctions;
import model.GameData;

import java.util.ArrayList;

public class ListGamesService {
    private final GameFunctions gameFunctions;
    private final AuthFunctions authFunctions;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameFunctions = new GameFunctions(gameDAO);
        this.authFunctions = new AuthFunctions(authDAO);
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException, DataAccessException {
        authFunctions.checkAuth(authToken);
        return gameFunctions.getGamesArray();
    }
}
