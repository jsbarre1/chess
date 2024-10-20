package functions;

import dataaccess.MemoryUserDAO;
import model.UserData;

public class UserFunctions {
    private MemoryUserDAO memoryUserDAO;
    public UserFunctions(MemoryUserDAO memoryUserDAO){
        this.memoryUserDAO = memoryUserDAO;
    }

    public UserData getUser(String username){
        return memoryUserDAO.getUser(username);
    }

    public void addUser(UserData userData){
        memoryUserDAO.addUser(userData);
    }

}
