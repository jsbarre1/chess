package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class ClearService {
    MemoryUserDAO memoryUserDAO;
    MemoryAuthDAO memoryAuthDAO;
    MemoryGameDAO memoryGameDAO;


    public ClearService(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.memoryUserDAO = memoryUserDAO;
        this.memoryAuthDAO = memoryAuthDAO;
        this.memoryGameDAO = memoryGameDAO;
    }

    public void deleteDB() throws DataAccessException {
        memoryUserDAO.deleteAllUsers();
        memoryGameDAO.deleteAllGames();
        memoryAuthDAO.deleteAllAuths();
    }
}
