package dataaccess;

import com.google.gson.Gson;
import exceptions.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO () throws ResponseException, DataAccessException {
        SQLFunctions sqlFunctions = new SQLFunctions();
        String[] createStatements = {
                """
           CREATE TABLE IF NOT EXISTS user (
             `username` VARCHAR(256) NOT NULL UNIQUE,
             `password` VARCHAR(256) NOT NULL,
             `email` VARCHAR(256) NOT NULL UNIQUE,
             `json` TEXT DEFAULT NULL,
             PRIMARY KEY (`username`),
             INDEX(password),
             INDEX(email)
           ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
           """
        };
        sqlFunctions.configureDatabase(createStatements);
    }

    public void deleteAllUsers() throws ResponseException {
        var statement = "TRUNCATE user";
        executeUpdateUser(statement);
    }

    public void addUser(UserData userData) throws DataAccessException, ResponseException {
        // First check if user already exists
        if (getUser(userData.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        try {
            var json = new Gson().toJson(userData);
            executeUpdateUser(statement, userData.username(), hashedPassword, userData.email(), json);
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE constraint") || e.getMessage().contains("duplicate")) {
                throw new DataAccessException("Error: already taken");
            }
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, email, json FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }


    private int executeUpdateUser(String userStatement, Object... userParams) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(userStatement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < userParams.length; i++) {
                    var userParam = userParams[i];
                    if (userParam instanceof String p) {ps.setString(i + 1, p);}
                    else if (userParam instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (userParam instanceof UserData p) {ps.setString(i + 1, p.toString());}
                    else if (userParam == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", userStatement, e.getMessage()));
        }
    }


    private UserData readUser(ResultSet readAuthResult) throws SQLException {
        var json = readAuthResult.getString("json");
        var userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }

}
