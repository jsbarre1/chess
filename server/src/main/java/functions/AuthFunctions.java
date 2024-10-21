package functions;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;

public class AuthFunctions {
    private MemoryAuthDAO memoryAuthDAO;
    public AuthFunctions(MemoryAuthDAO memoryAuthDAO){
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public boolean isNotAuthenticated(String authToken) throws DataAccessException {
        return memoryAuthDAO.getAuth(authToken) == null;
    }

    public void deleteAuth(String authToken)throws DataAccessException{
        memoryAuthDAO.deleteAuth(authToken);
    }

    public AuthData addAuth(String username)throws DataAccessException{
        return memoryAuthDAO.addAuth(username);
    }

}
