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
        if(authFunctions.isNotAuthenticated(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }else{
            authFunctions.deleteAuth(authToken);
            return new HashMap<>();
        }
    }


}
