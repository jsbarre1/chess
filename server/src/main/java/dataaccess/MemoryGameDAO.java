package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    final private Map<Integer, GameData> gameList;

    public MemoryGameDAO() {
        this.gameList = new HashMap<>();
    }

    public void deleteAllGames()throws DataAccessException{
        try{
            gameList.clear();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Map<Integer, GameData> getGames()throws DataAccessException{
        try{
            return gameList;
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public ArrayList<GameData> getGamesArray()throws DataAccessException{
        ArrayList<GameData> games = new ArrayList<>();
        try{
            for (Map.Entry<Integer, GameData> game : gameList.entrySet()) {
                GameData value = game.getValue();
                games.add(value);
            }
            return games;
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData addGame(GameData gameData) throws DataAccessException {
        try {
            gameList.put(gameData.gameID(), gameData);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return new GameData(gameData.gameID(), null, null, null, null);
    }

    public GameData getGame(Integer id) throws DataAccessException{
        GameData targetGame = gameList.get(id);

        if(targetGame == null){
            throw new DataAccessException("Game Not Found");
        }

        else{ return targetGame;}
    }

    public Map<Integer, GameData> addPlayerToGame(GameData oldGame, String playerColor, String username)throws DataAccessException {
        if(Objects.equals(playerColor, "WHITE")){
            GameData newGame = new GameData(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            gameList.remove(oldGame.gameID());
            gameList.put(newGame.gameID(), newGame);
            return new HashMap<>();
        }else{
            GameData newGame = new GameData(oldGame.gameID(),oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
            gameList.remove(oldGame.gameID());
            gameList.put(newGame.gameID(), newGame);
            return new HashMap<>();
        }
    }

    @Override
    public void setGame(Integer gameID, GameData game) throws DataAccessException {

    }

    @Override
    public void removePlayer(GameData oldGame, String playerColor, String username) throws DataAccessException {

    }

    ;


}
