package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ResponseException;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO ()throws ResponseException, DataAccessException {
        SQLFunctions sqlFunctions = new SQLFunctions();
        String[] createStatements = {
                """
           CREATE TABLE IF NOT EXISTS game (
             `gameID` INTEGER NOT NULL UNIQUE,
             `whiteUsername` VARCHAR(256) DEFAULT NULL,
             `blackUsername` VARCHAR(256) DEFAULT NULL,
             `gameName` VARCHAR(256) NOT NULL,
             `gameState` TEXT NOT NULL,
             `json` TEXT DEFAULT NULL,
             PRIMARY KEY (`gameID`)
           ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
           """
        };
        sqlFunctions.configureDatabase(createStatements);
    }

    public void deleteAllGames() throws ResponseException {
        var statement = "TRUNCATE game";
        executeUpdateGame(statement);
    }

    public Map<Integer, GameData> getGames() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    Map<Integer, GameData> games = new HashMap<>();
                    while (rs.next()) {
                        GameData game = readGame(rs);
                        games.put(game.gameID(), game);
                    }
                    return games;
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public ArrayList<GameData> getGamesArray() throws ResponseException {
        ArrayList<GameData> finalGames = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData game = readGame(rs);
                        finalGames.add(game);
                    }
                    return finalGames;
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }
    public GameData addGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, gameState, json) VALUES (?, ?, ?, ?, ?, ?)";
        try{
            var json = new Gson().toJson(gameData);
            executeUpdateGame(statement, gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), gameData, json);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameData;
    }

    public GameData getGame(Integer id) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public Map<Integer, GameData> addPlayerToGame(GameData oldGame, String playerColor, String username) throws DataAccessException {
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, json = ? WHERE gameID = ?";
        try{
            GameData newGame;
            if(Objects.equals(playerColor, "WHITE")){
                if(oldGame.whiteUsername() != null){
                    throw new DataAccessException("white player already occupied");
                }
                newGame = new GameData(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            }else{
                if(oldGame.blackUsername() != null){
                    throw new DataAccessException("black player already occupied");
                }
                newGame = new GameData(oldGame.gameID(),oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
            }

            var json = new Gson().toJson(newGame);
            executeUpdateGame(statement, newGame.whiteUsername(), newGame.blackUsername(), json, newGame.gameID());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return new HashMap<>();
    }

    @Override
    public void setGame(Integer gameID, ChessGame game) throws DataAccessException {
        Gson gson = new Gson();
        String stringGame = gson.toJson(game);

        String sql = "UPDATE games SET game = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, stringGame);
            preparedStatement.setInt(2, gameID);
            int numUpdated = preparedStatement.executeUpdate();

            if (numUpdated == 0) {
                throw new DataAccessException("Failed to update game with ID " + gameID + ". Game not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game in the database: " + e.getMessage());
        }
    }



    private int executeUpdateGame(String gameStatement, Object... gameParams) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(gameStatement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < gameParams.length; i++) {
                    var gameParam = gameParams[i];
                    if (gameParam instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (gameParam instanceof String p) {ps.setString(i + 1, p);}
                    else if (gameParam instanceof GameData p) {ps.setString(i + 1, p.toString());}
                    else if (gameParam == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", gameStatement, e.getMessage()));
        }
    }


    private GameData readGame(ResultSet readAuthResult) throws SQLException {
        var json = readAuthResult.getString("json");
        var gameData = new Gson().fromJson(json, GameData.class);
        return gameData;
    }
}

