package functions;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import model.GameData;

import java.util.Random;

public class GameFunctions {
    private MemoryGameDAO memoryGameDAO;

    public GameFunctions(MemoryGameDAO memoryGameDAO) {
        this.memoryGameDAO = memoryGameDAO;

    }

    public Object createGame(String gameName){
        Random random = new Random();
        GameData gameData = new GameData(random.nextInt(1,9999), null, null, gameName, new ChessGame());
        return memoryGameDAO.addGame(gameData);
    }
}
