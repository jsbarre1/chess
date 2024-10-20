package dataaccess;

import model.UserData;

public interface UserDAO {
    void deleteAllUsers() throws DataAccessException;
    void addUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
