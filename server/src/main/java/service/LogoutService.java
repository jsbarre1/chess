package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;

import java.util.HashMap;

public class LogoutService {
    private AuthFunctions authFunctions;
    public LogoutService(AuthDAO authDAO){
        this.authFunctions = new AuthFunctions(authDAO);
    }

    public Object logout(String authToken) throws ResponseException, DataAccessException {
        authFunctions.checkAuth(authToken);
        authFunctions.deleteAuth(authToken);
        return new HashMap<>();
    }


}
