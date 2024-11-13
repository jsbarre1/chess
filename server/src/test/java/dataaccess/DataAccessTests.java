package dataaccess;

import chess.ChessGame;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataAccessTests {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private final DataAccessException usernameTaken = new DataAccessException
            ("unable to update database: INSERT INTO auth (authToken, username, json) " +
                    "VALUES (?, ?, ?), Duplicate entry 'username' for key 'auth.username'");
    private final DataAccessException gameIdTaken = new DataAccessException
            ("unable to update database: " + "INSERT INTO game " +
                    "(gameID, whiteUsername, blackUsername, gameName, gameState, json) " +
                    "VALUES " + "(?, ?, ?, ?, ?, ?), Duplicate entry '1' for key 'game.PRIMARY'");
    private final DataAccessException whiteSlotTaken = new DataAccessException
            ("white player already occupied");

    @BeforeEach
    public void startDatabase() {
        try {
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
            userDAO = new SQLUserDAO();
            //drop all table contents
            authDAO.deleteAllAuths();
            userDAO.deleteAllUsers();
            gameDAO.deleteAllGames();
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Auth Add Success")
    public void addAuth (){
        AuthData authData;
        AuthData correctData;
        try {
             authData = authDAO.addAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        correctData = new AuthData("username", authData.authToken());

        Assertions.assertEquals(authData, correctData);
    }

    @Test
    @DisplayName("Auth Add Failure")
    public void addSameAuth (){
        try {
            AuthData authData = authDAO.addAuth("username");
            AuthData authData2 = authDAO.addAuth("username");

        } catch (DataAccessException e) {
            Assertions.assertEquals(e.toString(), usernameTaken.toString());
        }
    }

    @Test
    @DisplayName("Auth Get Success")
    public void getAuth (){
        AuthData correctAuth;
        AuthData getAuth;
        try {
            AuthData authData = authDAO.addAuth("username");
            correctAuth = new AuthData("username", authData.authToken());
            getAuth = authDAO.getAuth(authData.authToken());

        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(getAuth, correctAuth);
    }

    @Test
    @DisplayName("Auth Get Failure")
    public void getIncorrectAuth (){
        AuthData authData;
        AuthData authFail;
        try {
            authData = authDAO.addAuth("username");
            authFail = authDAO.getAuth("1");
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNull(authFail);
    }

    @Test
    @DisplayName("Auth Delete Success")
    public void deleteAuth (){
        AuthData authData;
        AuthData nullAuth;
        try {
            authData = authDAO.addAuth("username");
            authDAO.deleteAuth(authData.authToken());
            nullAuth = authDAO.getAuth(authData.authToken());

        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNull(nullAuth);
    }

    @Test
    @DisplayName("Auth Delete Failure")
    public void deleteAuthFail (){
        AuthData authData;
        AuthData nullAuth;
        try {
            authData = authDAO.addAuth("username");
            authDAO.deleteAuth("1");
            nullAuth = authDAO.getAuth(authData.authToken());

        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(nullAuth);
    }

    @Test
    @DisplayName("Auth Delete All")
    public void clearAuths() throws ResponseException, DataAccessException {
        AuthData authData = authDAO.addAuth("username");
        AuthData authData1 = authDAO.addAuth("username1");
        AuthData authData2 = authDAO.addAuth("username2");
        authDAO.deleteAllAuths();
        ArrayList<AuthData> authDataArrayList = new ArrayList<>();
        ArrayList<AuthData> expected = new ArrayList<>();
        expected.add(null);
        expected.add(null);
        expected.add(null);

        authDataArrayList.add(authDAO.getAuth(authData.authToken()));
        authDataArrayList.add(authDAO.getAuth(authData1.authToken()));
        authDataArrayList.add(authDAO.getAuth(authData2.authToken()));
        Assertions.assertEquals(expected , authDataArrayList);

    }

    @Test
    @DisplayName("User Add Success")
    public void addUser (){
        UserData expectedUserData;
        UserData actualUserData;
        try {
            UserData userData = new UserData("username", "password", "email");
            userDAO.addUser(userData);
            actualUserData = userDAO.getUser("username");
            expectedUserData = new UserData("username", "password", "email");
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(actualUserData, expectedUserData);
    }

    @Test
    @DisplayName("User Add Failure")
    public void addUserFail () throws DataAccessException, ResponseException {
        UserData expectedUserData;
        UserData actualUserData;
            UserData userData = new UserData("username", "password", "email");
            userDAO.addUser(userData);
            actualUserData = userDAO.getUser("username");
            expectedUserData = new UserData("username", "password", "email");
        Assertions.assertEquals(actualUserData, expectedUserData);
    }


    @Test
    @DisplayName("User get Success")
    public void getUser () throws DataAccessException, ResponseException {
        UserData expectedGetUser;
        UserData actualGetUser;
        UserData userData = new UserData("username", "password", "email");
        userDAO.addUser(userData);actualGetUser = userDAO.getUser("username");
        expectedGetUser = new UserData("username", "password", "email");
        Assertions.assertEquals(expectedGetUser, actualGetUser);
    }

    @Test
    @DisplayName("User get Failure")
    public void getUserFail () throws DataAccessException, ResponseException {
        UserData userData = new UserData("username", "password", "email");
        userDAO.addUser(userData);
        UserData actualGetUser = userDAO.getUser("name");
        Assertions.assertNull(actualGetUser);
    }

    @Test
    @DisplayName("Users Delete All")
    public void clearUsers() throws ResponseException, DataAccessException {
        userDAO.addUser(new UserData("user1", "password", "user1@gmail.com"));
        userDAO.addUser(new UserData("user2", "password", "user2@gmail.com"));
        userDAO.addUser(new UserData("user3", "password", "user3@gmail.com"));

        userDAO.deleteAllUsers();
        ArrayList<UserData> userDataArrayList = new ArrayList<>();
        ArrayList<UserData> expected = new ArrayList<>();
        expected.add(null);
        expected.add(null);
        expected.add(null);

        userDataArrayList.add(userDAO.getUser("user1"));
        userDataArrayList.add(userDAO.getUser("user2"));
        userDataArrayList.add(userDAO.getUser("user3"));
        Assertions.assertEquals(expected , userDataArrayList);
    }

    @Test
    @DisplayName("Game Add Success")
    public void addGame () throws DataAccessException, ResponseException {
        GameData expectedGameData;
        GameData actualGameData;
        GameData gameData = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(gameData);
        actualGameData = gameDAO.getGame(1);
        expectedGameData = new GameData(1, null, null, "game1", new ChessGame());
        Assertions.assertEquals(actualGameData, expectedGameData);
    }

    @Test
    @DisplayName("Game Add Failure")
    public void addGameFail (){
        GameData gameData = new GameData(1, null, null, "game1", new ChessGame());
        GameData gameData2 = new GameData(1, null, null, "game2", new ChessGame());

        try {
            gameDAO.addGame(gameData);
            gameDAO.addGame(gameData2);
        } catch (DataAccessException e) {
            Assertions.assertEquals(gameIdTaken.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("Game Get Success")
    public void getGame () throws DataAccessException, ResponseException {
        GameData correctGame = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(correctGame);
        GameData getGame = gameDAO.getGame(1);
        Assertions.assertEquals(correctGame, getGame);
    }

    @Test
    @DisplayName("Game Get Failure")
    public void getGameFail () throws DataAccessException, ResponseException {
        GameData correctGame = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(correctGame);
        GameData getGame = gameDAO.getGame(2);
        Assertions.assertNull(getGame);
    }

    @Test
    @DisplayName("Games Get Success (Hash Map)")
    public void getGamesMap() throws DataAccessException, ResponseException {
        GameData game1 = new GameData(1, null, null, "game1", new ChessGame());
        GameData game2 = new GameData(2, null, null, "game2", new ChessGame());
        gameDAO.addGame(game1);
        gameDAO.addGame(game2);

        Map<Integer, GameData> expected = new HashMap<>();
        expected.put(1, game1);
        expected.put(2, game2);

        Map<Integer, GameData> actual = gameDAO.getGames();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Games Get Failure (Hash Map)")
    public void getGamesMapFail() {
        GameData game1 = new GameData(1, null, null, "game1", new ChessGame());
        GameData game2 = new GameData(1, null, null, "game2", new ChessGame());
        try {
            gameDAO.addGame(game1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            gameDAO.addGame(game2);
        } catch (DataAccessException e) {
            Assertions.assertEquals(gameIdTaken.getMessage(), e.getMessage());
        }

        Map<Integer, GameData> expected = new HashMap<>();
        expected.put(1, game1);
        Map<Integer, GameData> actual = null;
        try {
            actual = gameDAO.getGames();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Games Get Success (Array)")
    public void getGamesArray() throws DataAccessException, ResponseException {
        GameData game1Data = new GameData(1, null, null, "game1", new ChessGame());
        GameData game2Data = new GameData(2, null, null, "game2", new ChessGame());
        gameDAO.addGame(game1Data);
        gameDAO.addGame(game2Data);

        ArrayList<GameData> expectedArray = new ArrayList<>();
        expectedArray.add(game1Data);
        expectedArray.add(game2Data);
        ArrayList<GameData> actualArray = gameDAO.getGamesArray();
        Assertions.assertEquals(expectedArray, actualArray);
    }

    @Test
    @DisplayName("Games Get Failure (Array)")
    public void getGamesArrayFail() {
        GameData gameData1 = new GameData(1, null, null, "game1", new ChessGame());
        GameData gameData2 = new GameData(1, null, null, "game2", new ChessGame());
        try {
            gameDAO.addGame(gameData1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            gameDAO.addGame(gameData2);
        } catch (DataAccessException e) {
            Assertions.assertEquals(gameIdTaken.getMessage(), e.getMessage());
        }

        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(gameData1);
        ArrayList<GameData> actual = new ArrayList<>();
        try {
            actual = gameDAO.getGamesArray();
        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected, actual);
    }


    @Test
    @DisplayName("Game Add Player Success")
    public void addPlayerToGame() throws DataAccessException, ResponseException {
        GameData gameData1 = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(gameData1);
        gameDAO.addPlayerToGame(gameData1, "WHITE", "username");

        GameData expected = new GameData(1, "username", null, "game1", new ChessGame());

        GameData actual = gameDAO.getGame(1);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    @DisplayName("Game Add Player Fail")
    public void addPlayerToGameWith(){
        GameData gameData1 = new GameData(1, null, null, "game1", new ChessGame());
        try {
            gameDAO.addGame(gameData1);
            gameDAO.addPlayerToGame(gameData1, "WHITE", "username");
            GameData whitePlayerIn = gameDAO.getGame(1);
            gameDAO.addPlayerToGame(whitePlayerIn, "WHITE", "shawtybae");
        } catch (DataAccessException | ResponseException e) {
            Assertions.assertEquals(whiteSlotTaken.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("Games Delete All")
    public void clearGames() throws ResponseException, DataAccessException {
        gameDAO.addGame(new GameData(1, null,null, "hehe", new ChessGame()));
        gameDAO.addGame(new GameData(2, null,null, "baha", new ChessGame()));
        gameDAO.addGame(new GameData(3, null,null, "haha", new ChessGame()));
        gameDAO.deleteAllGames();
        ArrayList<GameData> userDataArrayList = new ArrayList<>();
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(null);
        expected.add(null);
        expected.add(null);

        userDataArrayList.add(gameDAO.getGame(1));
        userDataArrayList.add(gameDAO.getGame(2));
        userDataArrayList.add(gameDAO.getGame(3));
        Assertions.assertEquals(expected , userDataArrayList);
    }



}


