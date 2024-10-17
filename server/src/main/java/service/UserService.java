package service;

import dataaccess.MemoryUserDAO;

public class UserService {
    MemoryUserDAO memoryUserDAO;

    public UserService(MemoryUserDAO memoryUserDAO){
        this.memoryUserDAO = memoryUserDAO;
    }
}
