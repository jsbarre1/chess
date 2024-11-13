package functions;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import exceptions.ResponseException;
import model.AuthData;

public class AuthFunctions {
    private AuthDAO authDAO;
    public AuthFunctions(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void checkAuth(String authToken) throws ResponseException, DataAccessException {
        if(authDAO.getAuth(authToken) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException, ResponseException {
        authDAO.deleteAuth(authToken);
    }

    public AuthData addAuth(String username)throws DataAccessException{
        return authDAO.addAuth(username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException, ResponseException {
        return authDAO.getAuth(authToken);
    }

}
