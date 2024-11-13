package service;

import chess.ChessGame;
import dataaccess.*;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import models.JoinGameObject;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


public class ServiceTests {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private Services services;
    private static UserData testUser = new UserData("Test", "Testing", "Test@gmail.com");

    private AuthData authData;
    private final ResponseException unauthorized = new ResponseException(401, "Error: unauthorized");
    private final ResponseException alreadyTaken = new ResponseException(403, "Error: already taken");

    @BeforeEach
    public void setUp() throws ResponseException, DataAccessException{
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
        authDAO.deleteAllAuths();
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();

        services = new Services(userDAO,authDAO,gameDAO);
        authData = services.getRegisterService().addUser(testUser);
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutUser() throws ResponseException, DataAccessException{
        Assertions.assertEquals(services.getLogoutService().logout(authData.authToken()), new HashMap<>());
    }

    @Test
    @DisplayName("Logout Fails (Bad AuthToken)")
    public void logoutFails(){
        try {
            services.getLogoutService().logout("123");
        } catch (ResponseException | DataAccessException e) {
            Assertions.assertEquals(e.toString(), unauthorized.toString());
        }
    }

    @Test
    @DisplayName("Clear Database")
    public void clear() throws DataAccessException, ResponseException {
        AuthData johnAuthData = services.getRegisterService().addUser(new UserData("john", "barrett", "john@gmail.com"));
        services.getCreateGameService().createGame(johnAuthData.authToken(), "CHESSSSS");
        services.getClearService().deleteDB();

        ArrayList<GameData> emptyArray = new ArrayList<>();

        Assertions.assertEquals(null, authDAO.getAuth(johnAuthData.authToken()));
        Assertions.assertEquals(null, authDAO.getAuth(authData.authToken()));
        Assertions.assertEquals("{}", gameDAO.getGames().toString());
        Assertions.assertEquals(emptyArray.toString(), gameDAO.getGamesArray().toString());
        Assertions.assertEquals(null, userDAO.getUser(johnAuthData.authToken()));
        Assertions.assertEquals(null, userDAO.getUser(authData.authToken()));
    }

    @Test
    @DisplayName("Login Success")
    public void login() throws ResponseException, DataAccessException {
        services.getLogoutService().logout(authData.authToken());
        AuthData newAuthData = services.getLoginService().login(new UserData("Test", "Testing", null));
        Assertions.assertNotNull(authDAO.getAuth(newAuthData.authToken()));
    }

    @Test
    @DisplayName("Login Failure (wrong password")
    public void loginFailure() throws ResponseException, DataAccessException {
        services.getLogoutService().logout(authData.authToken());
        try {
            AuthData newAuthData = services.getLoginService().login(new UserData("Test", "incorrect", null));
        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), unauthorized.toString());
        }
    }

    @Test
    @DisplayName("Register Success")
    public void register() throws ResponseException, DataAccessException {
        AuthData newAuthData = services.getRegisterService().addUser(new UserData("Dave", "Matthews", "Dave@gmail.com"));
        Assertions.assertNotNull(authDAO.getAuth(newAuthData.authToken()));
        Assertions.assertNotNull(userDAO.getUser(newAuthData.username()));
    }

    @Test
    @DisplayName("Register Failure (username exists")
    public void registerFail() throws ResponseException, DataAccessException {
        try {
            AuthData newAuthData = services.getRegisterService().addUser(new UserData("Test", "Testing", "Test@gmail.com"));
        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), alreadyTaken.toString());
        }
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGame() throws ResponseException, DataAccessException {
        GameData gameID = services.getCreateGameService().createGame(authData.authToken(), "newGame");
        GameData correctGame = new GameData(gameID.gameID(), null, null, "newGame", new ChessGame());
        Assertions.assertEquals(gameDAO.getGame(gameID.gameID()), correctGame);
    }

    @Test
    @DisplayName("Create Game Fail (unauthorized)")
    public void createGameFail() throws DataAccessException {
       try {
           GameData gameID = services.getCreateGameService().createGame("123", "newGame");
       } catch (ResponseException e) {
           Assertions.assertEquals(e.toString(), unauthorized.toString());
       }
    }

    @Test
    @DisplayName("List Games Success")
    public void listGames() throws DataAccessException, ResponseException {
        GameData gameId1 = services.getCreateGameService().createGame(authData.authToken(), "hello");
        GameData gameId2 = services.getCreateGameService().createGame(authData.authToken(), "good");

        ArrayList<GameData> games = services.getListGamesService().listGames(authData.authToken());

        ArrayList<GameData> correctGames = new ArrayList<>();
        correctGames.add( new GameData(gameId1.gameID(), null, null, "hello", new ChessGame()));
        correctGames.add( new GameData(gameId2.gameID(), null, null, "good", new ChessGame()));

        games.sort(Comparator.comparing(GameData::gameID));
        correctGames.sort(Comparator.comparing(GameData::gameID));

        Assertions.assertEquals(correctGames.toString(), games.toString());
    }

    @Test
    @DisplayName("List Games Failure (not authenticated)")
    public void listGamesFailure() throws DataAccessException, ResponseException {
        GameData gameID1 = services.getCreateGameService().createGame(authData.authToken(), "hello");
        GameData gameID2 = services.getCreateGameService().createGame(authData.authToken(), "goodbye");

        try {
            ArrayList<GameData> games = services.getListGamesService().listGames("123");
        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), unauthorized.toString());
        }

    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGame() throws ResponseException, DataAccessException {
        GameData gameID1 = services.getCreateGameService().createGame(authData.authToken(), "hello");
        services.getJoinGameService().joinGame(authData.authToken(), new JoinGameObject("WHITE", gameID1.gameID()));

        GameData correctGame = new GameData(gameID1.gameID(), authData.username(), null, "hello", new ChessGame());
        GameData actualGame = gameDAO.getGame(gameID1.gameID());
        Assertions.assertEquals(actualGame.toString(), correctGame.toString());
    }

    @Test
    @DisplayName("Join Game Fail (spot taken)")
    public void joinGameFail() throws ResponseException, DataAccessException {
        GameData gameID1 = services.getCreateGameService().createGame(authData.authToken(), "hello");
        services.getJoinGameService().joinGame(authData.authToken(), new JoinGameObject("WHITE", gameID1.gameID()));

        try {
            services.getJoinGameService().joinGame(authData.authToken(), new JoinGameObject("WHITE", gameID1.gameID()));

        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), alreadyTaken.toString());
        }
    }






}
