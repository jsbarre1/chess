package client;

import dataaccess.*;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import server.Server;
import server.ServerFacade;
import service.Services;


public class ServerFacadeTests {
    UserData testUser = new UserData("username", "password", "email");
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private Services services;
    private static Server server;
    private static ServerFacade serverFace;
    private AuthData loggedIn;
    private String testAuthToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        serverFace = new ServerFacade("http://localhost:8080");
    }

    @BeforeEach
    public void setUp() throws ResponseException, DataAccessException {
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
        authDAO.deleteAllAuths();
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
        loggedIn = serverFace.registerUser(testUser);
        testAuthToken= loggedIn.authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws ResponseException, DataAccessException {
        AuthData expected = authDAO.getAuth(loggedIn.authToken());
        Assertions.assertEquals(expected, loggedIn);
    }

    @Test
    public void logout () throws ResponseException, DataAccessException {
        AuthData authData = new AuthData(null, testAuthToken);
        serverFace.logoutUser(authData);
        Assertions.assertNull(authDAO.getAuth(testAuthToken));
    }
    @Test
    public void login() throws ResponseException, DataAccessException {
        AuthData actual = serverFace.loginUser(testUser);
        AuthData expected = authDAO.getAuth(actual.authToken());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createGame() throws ResponseException, DataAccessException {
        CreateGameRequest response = serverFace.createChessGame(new CreateGameRequest("game"));


    }

}
