package dataaccess;

import com.google.gson.Gson;
import exceptions.ResponseException;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO () throws ResponseException, DataAccessException {
        SQLFunctions sqlFunctions = new SQLFunctions();
        String[] createStatements = {
                """
           CREATE TABLE IF NOT EXISTS auth (
             `authToken` VARCHAR(256) NOT NULL UNIQUE,
             `username` VARCHAR(256) NOT NULL,
             `json` TEXT DEFAULT NULL,
             PRIMARY KEY (`authToken`),
             INDEX(username)
           ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
           """
        };
        sqlFunctions.configureDatabase(createStatements);
    }

    public void deleteAllAuths() throws ResponseException {
        var statement = "TRUNCATE auth";
        executeUpdateAuth(statement);
    }

    public AuthData addAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username, json) VALUES (?, ?, ?)";
        AuthData authData = new AuthData(username, UUID.randomUUID().toString());
        try{
            var json = new Gson().toJson(authData);
            executeUpdateAuth(statement, authData.authToken(), authData.username(), json);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        return authData;
    }

    public AuthData getAuth(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws ResponseException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdateAuth(statement, authToken);
    }


    private int executeUpdateAuth(String authStatement, Object... authParams) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(authStatement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < authParams.length; i++) {
                    var authParam = authParams[i];
                    if (authParam instanceof String p) {ps.setString(i + 1, p);}
                    else if (authParam instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (authParam instanceof AuthData p) {ps.setString(i + 1, p.toString());}
                    else if (authParam == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", authStatement, e.getMessage()));
        }
    }

    private AuthData readAuth(ResultSet readAuthResult) throws SQLException {
        var authToken = readAuthResult.getString("authToken");
        var json = readAuthResult.getString("json");
        var authData = new Gson().fromJson(json, AuthData.class);
        return authData.setAuthToken(authToken);
    }
}
