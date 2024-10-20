package functions;

import dataaccess.MemoryAuthDAO;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class AuthFunctions {
    private MemoryAuthDAO memoryAuthDAO;
    public AuthFunctions(MemoryAuthDAO memoryAuthDAO){
        this.memoryAuthDAO = memoryAuthDAO;
    }
    public boolean isAuthenticated(String authToken){
        if(memoryAuthDAO.getAuth(authToken)==null){
            return false;
        }else{
            memoryAuthDAO.deleteAuth(authToken);
            return true;
        }
    }

    public void deleteAuth(String authToken){
        memoryAuthDAO.deleteAuth(authToken);
    }

    public AuthData addAuth(String username){
        return memoryAuthDAO.addAuth(username);
    }

}
