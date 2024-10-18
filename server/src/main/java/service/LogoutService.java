package service;

import dataaccess.MemoryAuthDAO;
import exceptions.ResponseException;

import java.util.HashMap;

public class LogoutService {
    private MemoryAuthDAO memoryAuthDAO;
    public LogoutService(MemoryAuthDAO memoryAuthDAO){
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public Object logout(String authToken) throws ResponseException {
        if(memoryAuthDAO.getAuth(authToken)==null){
            throw new ResponseException(401, "Error: unauthorized");
        }else{
            memoryAuthDAO.deleteAuth(authToken);
            return new HashMap<>();
        }
    }


}
