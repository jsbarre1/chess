package service;

import dataaccess.MemoryAuthDAO;

public class AuthService {
    MemoryAuthDAO memoryAuthDAO;

    public AuthService(MemoryAuthDAO memoryAuthDAO){
        this.memoryAuthDAO = memoryAuthDAO;
    }
}
