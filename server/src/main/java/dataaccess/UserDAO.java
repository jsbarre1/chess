package dataaccess;

import exceptions.ResponseException;
import model.UserData;

public interface UserDAO {
    void deleteAllUsers() throws DataAccessException, ResponseException;
    void addUser(UserData userData) throws DataAccessException, ResponseException;
    UserData getUser(String username) throws DataAccessException, ResponseException;
}
