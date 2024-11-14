package client;

import dataaccess.*;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import server.Server;
import ui.ServerFacade;

import java.util.ArrayList;


public class ServerFacadeTests {
    UserData testUser = new UserData("username", "password", "email");
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private static Server server;
    private static ServerFacade serverFace;
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
        UserDAO userDAO = new SQLUserDAO();
        authDAO.deleteAllAuths();
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
        AuthData loggedIn = serverFace.registerUser(testUser);
        testAuthToken= loggedIn.authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register(){
        Assertions.assertTrue(testAuthToken.length() > 10);
    }

    @Test
    public void registerFail() {
        try {
            serverFace.registerUser(testUser);
        }catch (ResponseException e){
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void logout () throws ResponseException, DataAccessException {
        serverFace.logoutUser();
        Assertions.assertNull(authDAO.getAuth(testAuthToken));
    }

    @Test
    public void logoutFail () throws ResponseException {
        serverFace.logoutUser();
        try{
            serverFace.logoutUser();
        }catch (ResponseException e){
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void login() throws ResponseException, DataAccessException {
        AuthData actual = serverFace.loginUser(testUser);
        AuthData expected = authDAO.getAuth(actual.authToken());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void loginFail(){
        try{
            UserData userData = new UserData("BBABFSBDFSD", "SPEIF", "pdsj");
            serverFace.loginUser(userData);
        } catch (ResponseException e) {
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void createGame() throws ResponseException{
        CreateGameResponse response = serverFace.createChessGame(new CreateGameRequest("game"));
        Assertions.assertNotNull(response);
    }

    @Test
    public void createGameFail() throws ResponseException{
        serverFace.logoutUser();

        try{
            serverFace.createChessGame(new CreateGameRequest("blub"));
        } catch (ResponseException e) {
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void listGames() throws ResponseException{
        ArrayList<GameData> response = serverFace.listChessGames();
        Assertions.assertNotNull(response);
    }

    @Test
    public void listGamesFail() throws ResponseException{
        serverFace.logoutUser();
        try{
            serverFace.createChessGame(new CreateGameRequest("blub"));
        } catch (ResponseException e) {
            Assertions.assertNotNull(e);
        }
    }

    @Test
    public void joinGame() throws ResponseException, DataAccessException {
        CreateGameResponse response = serverFace.createChessGame(new CreateGameRequest("game"));
        JoinGameRequest request = new JoinGameRequest("WHITE", response.gameID());
        serverFace.joinGame(request);
       GameData gameData = gameDAO.getGame(response.gameID());
        Assertions.assertNotNull(gameData.whiteUsername());

    }

    @Test
    public void joinGameFail() throws ResponseException {
        CreateGameResponse response = serverFace.createChessGame(new CreateGameRequest("game"));
        JoinGameRequest request = new JoinGameRequest("WHITE", response.gameID());
        serverFace.joinGame(request);

        try{
            serverFace.joinGame(request);
        }catch (ResponseException e) {
            Assertions.assertNotNull(e);

        }
    }





}
