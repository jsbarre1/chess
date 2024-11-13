package dataaccess;

import exceptions.ResponseException;
import model.AuthData;

public interface AuthDAO {
    void deleteAllAuths() throws DataAccessException, ResponseException;
    AuthData addAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException, ResponseException;
    void deleteAuth(String authToken) throws DataAccessException, ResponseException;
}
