package service;

import dataaccess.MemoryGameDAO;

public class GameService {
    MemoryGameDAO memoryGameDAO;

    public GameService(MemoryGameDAO memoryGameDAO){
        this.memoryGameDAO = memoryGameDAO;
    }
}
