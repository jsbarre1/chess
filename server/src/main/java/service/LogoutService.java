package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;

import java.util.HashMap;

public class LogoutService {
    private AuthFunctions authFunctions;
    public LogoutService(MemoryAuthDAO memoryAuthDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
    }

    public Object logout(String authToken) throws ResponseException, DataAccessException {
        authFunctions.checkAuth(authToken);
        authFunctions.deleteAuth(authToken);
        return new HashMap<>();
    }


}
