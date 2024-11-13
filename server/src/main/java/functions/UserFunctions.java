package functions;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import exceptions.ResponseException;
import model.UserData;

public class UserFunctions {
    private UserDAO userDAO;
    public UserFunctions(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public UserData getUser(String username) throws DataAccessException, ResponseException {
        return userDAO.getUser(username);
    }

    public void addUser(UserData userData) throws DataAccessException{
        userDAO.addUser(userData);
    }

}
