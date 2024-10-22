package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import models.JoinGameObject;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ServiceTests {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private ClearService clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    private RegisterService registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
    private LoginService loginService = new LoginService(memoryAuthDAO, memoryUserDAO);
    private CreateGameService createGameService = new CreateGameService(memoryAuthDAO,memoryGameDAO);
    private LogoutService logoutService = new LogoutService(memoryAuthDAO);
    private ListGamesService listGamesService = new ListGamesService(memoryGameDAO, memoryAuthDAO);
    private JoinGameService joinGameService = new JoinGameService(memoryGameDAO, memoryAuthDAO);

    private static UserData testUser = new UserData("Test", "Testing", "Test@gmail.com");

    private AuthData authData;
    private final ResponseException unauthorized = new ResponseException(401, "Error: unauthorized");
    private final ResponseException alreadyTaken = new ResponseException(403, "Error: already taken");

    @BeforeEach
    public void setUp() throws ResponseException, DataAccessException{
        authData = registerService.addUser(testUser);
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutUser() throws ResponseException, DataAccessException{
        Assertions.assertEquals(logoutService.logout(authData.authToken()), new HashMap<>());
    }

    @Test
    @DisplayName("Logout Fails (Bad AuthToken)")
    public void logoutFails(){
        try {
            logoutService.logout("123");
        } catch (ResponseException | DataAccessException e) {
            Assertions.assertEquals(e.toString(), unauthorized.toString());
        }
    }

    @Test
    @DisplayName("Clear Database")
    public void clear() throws DataAccessException, ResponseException {
        AuthData johnAuthData = registerService.addUser(new UserData("john", "barrett", "john@gmail.com"));
        createGameService.createGame(johnAuthData.authToken(), "CHESSSSS");
        clearService.deleteDB();

        Assertions.assertEquals(null, memoryAuthDAO.getAuth(johnAuthData.authToken()));
        Assertions.assertEquals(null, memoryAuthDAO.getAuth(authData.authToken()));
        Assertions.assertEquals("{}", memoryGameDAO.getGames().toString());
        Assertions.assertEquals(null, memoryUserDAO.getUser(johnAuthData.authToken()));
        Assertions.assertEquals(null, memoryUserDAO.getUser(authData.authToken()));
    }

    @Test
    @DisplayName("Login Success")
    public void login() throws ResponseException, DataAccessException {
        logoutService.logout(authData.authToken());
        AuthData newAuthData = loginService.login(new UserData("Test", "Testing", null));
        Assertions.assertNotNull(memoryAuthDAO.getAuth(newAuthData.authToken()));
    }

    @Test
    @DisplayName("Login Failure (wrong password")
    public void loginFailure() throws ResponseException, DataAccessException {
        logoutService.logout(authData.authToken());
        try {
            AuthData newAuthData = loginService.login(new UserData("Test", "incorrect", null));
        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), unauthorized.toString());
        }
    }

    @Test
    @DisplayName("Register Success")
    public void register() throws ResponseException, DataAccessException {
        AuthData newAuthData = registerService.addUser(new UserData("Dave", "Matthews", "Dave@gmail.com"));
        Assertions.assertNotNull(memoryAuthDAO.getAuth(newAuthData.authToken()));
        Assertions.assertNotNull(memoryUserDAO.getUser(newAuthData.username()));
    }

    @Test
    @DisplayName("Register Failure (username exists")
    public void registerFail() throws ResponseException, DataAccessException {
        try {
            AuthData newAuthData = registerService.addUser(new UserData("Test", "Testing", "Test@gmail.com"));
        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), alreadyTaken.toString());
        }
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGame() throws ResponseException, DataAccessException {
        GameData gameID = createGameService.createGame(authData.authToken(), "newGame");
        GameData correctGame = new GameData(gameID.gameID(), null, null, "newGame", new ChessGame());
        Assertions.assertEquals(memoryGameDAO.getGame(gameID.gameID()), correctGame);
    }

    @Test
    @DisplayName("Create Game Fail (unauthorized)")
    public void createGameFail() throws DataAccessException {
       try {
           GameData gameID = createGameService.createGame("123", "newGame");
       } catch (ResponseException e) {
           Assertions.assertEquals(e.toString(), unauthorized.toString());
       }
    }

    @Test
    @DisplayName("List Games Success")
    public void listGames() throws DataAccessException, ResponseException {
        GameData gameId1 = createGameService.createGame(authData.authToken(), "hello");
        GameData gameId2 = createGameService.createGame(authData.authToken(), "goodbye");

        ArrayList<GameData> games = listGamesService.listGames(authData.authToken());

        ArrayList<GameData> correctGames = new ArrayList<>();
        correctGames.add( new GameData(gameId1.gameID(), null, null, "hello", new ChessGame()));
        correctGames.add( new GameData(gameId2.gameID(), null, null, "goodbye", new ChessGame()));

        Assertions.assertEquals(games, correctGames);
    }

    @Test
    @DisplayName("List Games Failure (not authenticated)")
    public void listGamesFailure() throws DataAccessException, ResponseException {
        GameData gameID1 = createGameService.createGame(authData.authToken(), "hello");
        GameData gameID2 = createGameService.createGame(authData.authToken(), "goodbye");

        try {
            ArrayList<GameData> games = listGamesService.listGames("123");
        } catch (ResponseException e) {
            Assertions.assertEquals(e.toString(), unauthorized.toString());
        }

    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGame() throws ResponseException, DataAccessException {
        GameData gameID1 = createGameService.createGame(authData.authToken(), "hello");
        joinGameService.joinGame(authData.authToken(), new JoinGameObject("WHITE", gameID1.gameID()));

        GameData correctGame = new GameData(gameID1.gameID(), authData.username(), null, "hello", new ChessGame());
        GameData actualGame = memoryGameDAO.getGame(gameID1.gameID());
        Assertions.assertEquals(actualGame.toString(), correctGame.toString());
    }




}
