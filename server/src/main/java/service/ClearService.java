package service;

import dataaccess.*;
import exceptions.ResponseException;

public class ClearService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;


    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void deleteDB() throws DataAccessException, ResponseException {
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
        authDAO.deleteAllAuths();
    }
}
