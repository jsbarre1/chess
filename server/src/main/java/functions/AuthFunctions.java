package functions;

import dataaccess.MemoryAuthDAO;
import model.AuthData;

public class AuthFunctions {
    private MemoryAuthDAO memoryAuthDAO;
    public AuthFunctions(MemoryAuthDAO memoryAuthDAO){
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public boolean isNotAuthenticated(String authToken){
        return memoryAuthDAO.getAuth(authToken) == null;
    }

    public void deleteAuth(String authToken){
        memoryAuthDAO.deleteAuth(authToken);
    }

    public AuthData addAuth(String username){
        return memoryAuthDAO.addAuth(username);
    }

}
