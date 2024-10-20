package functions;

import dataaccess.MemoryGameDAO;

public class GameFunctions {
    private MemoryGameDAO memoryGameDAO;

    public GameFunctions(MemoryGameDAO memoryGameDAO) {
        this.memoryGameDAO = memoryGameDAO;
    }
}
