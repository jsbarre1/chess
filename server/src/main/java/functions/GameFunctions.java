package functions;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import exceptions.ResponseException;
import model.GameData;
import models.JoinGameObject;

import java.util.*;

public class GameFunctions {
    private GameDAO gameDAO;

    public GameFunctions(GameDAO gameDAO) {
        this.gameDAO = gameDAO;

    }

    public GameData createGame(String gameName) throws DataAccessException {
        Random random = new Random();
        GameData gameData = new GameData(random.nextInt(1,9999), null, null, gameName, new ChessGame());
        return gameDAO.addGame(gameData);
    }

    public Map<Integer, GameData> getGames() throws DataAccessException, ResponseException {
        return gameDAO.getGames();
    }

    public ArrayList<GameData> getGamesArray() throws DataAccessException, ResponseException {
        return gameDAO.getGamesArray();
    }

    public GameData getGame(Integer gameID) throws DataAccessException, ResponseException {
        return gameDAO.getGame(gameID);
    }


    public Map<Integer, GameData> joinGame(GameData oldGame, String playerColor, String username) throws DataAccessException{
       return gameDAO.addPlayerToGame(oldGame, playerColor, username);
    }

    public void alreadyJoined(GameData oldGame, JoinGameObject colorAndId) throws ResponseException{
        if(Objects.equals(colorAndId.playerColor(), "WHITE")){
            if(oldGame.whiteUsername() != null){
                throw new ResponseException(403, "Error: already taken");
            }
        }
        if(Objects.equals(colorAndId.playerColor(), "BLACK")){
            if(oldGame.blackUsername() != null){
                throw new ResponseException(403, "Error: already taken");
            }
        }
    }

}
