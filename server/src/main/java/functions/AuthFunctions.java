package functions;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import exceptions.ResponseException;
import model.AuthData;

public class AuthFunctions {
    private MemoryAuthDAO memoryAuthDAO;
    public AuthFunctions(MemoryAuthDAO memoryAuthDAO){
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public void checkAuth(String authToken) throws ResponseException, DataAccessException {
        if(memoryAuthDAO.getAuth(authToken) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void deleteAuth(String authToken)throws DataAccessException{
        memoryAuthDAO.deleteAuth(authToken);
    }

    public AuthData addAuth(String username)throws DataAccessException{
        return memoryAuthDAO.addAuth(username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        return memoryAuthDAO.getAuth(authToken);
    }

}
