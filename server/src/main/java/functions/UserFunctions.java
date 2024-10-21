package functions;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;

public class UserFunctions {
    private MemoryUserDAO memoryUserDAO;
    public UserFunctions(MemoryUserDAO memoryUserDAO){
        this.memoryUserDAO = memoryUserDAO;
    }

    public UserData getUser(String username) throws DataAccessException {
        return memoryUserDAO.getUser(username);
    }

    public void addUser(UserData userData) throws DataAccessException{
        memoryUserDAO.addUser(userData);
    }

}
