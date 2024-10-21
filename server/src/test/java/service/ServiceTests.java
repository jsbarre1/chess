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
import org.junit.jupiter.api.*;
import passoff.exception.TestException;
import passoff.model.TestResult;
import spark.Response;

import java.util.HashMap;
import java.util.Locale;


public class ServiceTests {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private ClearService clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    private RegisterService registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
    private LoginService loginService = new LoginService(memoryAuthDAO, memoryUserDAO);
    private CreateGameService createGameService = new CreateGameService(memoryAuthDAO,memoryGameDAO);
    private LogoutService logoutService = new LogoutService(memoryAuthDAO);
    private static UserData testUser;
    private AuthData authData;
    private final ResponseException unauthorized = new ResponseException(401, "Error: unauthorized");
    private final ResponseException alreadyTaken = new ResponseException(403, "Error: already taken");

    //completed:
    //logoutService
    //clearService
    //loginService
    //registerService

    @BeforeAll
    public static void init(){
        //test user
        testUser = new UserData("Test", "Testing", "Test@gmail.com");

    }

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
        new GameData(gameID.gameID(), null, null, "newGame", new ChessGame());
        Assertions.assertEquals(memoryGameDAO.getGame);
    }




}
