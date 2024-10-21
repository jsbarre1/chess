package functions;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameFunctions {
    private MemoryGameDAO memoryGameDAO;

    public GameFunctions(MemoryGameDAO memoryGameDAO) {
        this.memoryGameDAO = memoryGameDAO;

    }

    public GameData createGame(String gameName) throws DataAccessException {
        Random random = new Random();
        GameData gameData = new GameData(random.nextInt(1,9999), null, null, gameName, new ChessGame());
        return memoryGameDAO.addGame(gameData);
    }

    public Map<Integer, GameData> getGames() throws DataAccessException{
        return memoryGameDAO.getGames();
    }

    public GameData getGame(Integer gameId) throws DataAccessException {
        return memoryGameDAO.getGame(gameId);
    }


    public Map<Integer, GameData> joinGame(GameData oldGame, String playerColor, String username) throws DataAccessException{
       return memoryGameDAO.addPlayerToGame(oldGame, playerColor, username);
    }
}
