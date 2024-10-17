package dataaccess;

import model.UserData;

public interface UserDAO {
    void deleteAllUsers() throws DataAccessException;
    UserData addUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
