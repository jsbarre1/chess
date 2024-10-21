package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.GameData;
import model.UserData;
import service.*;
import spark.*;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    private ClearService clearService;
    private RegisterService registerService;
    private LogoutService logoutService;
    private LoginService loginService;
    private ListGamesService listGamesService;
    private CreateGameService createGameService;
;
    public Server() {
        this.clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        this.registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
        this.logoutService = new LogoutService(memoryAuthDAO);
        this.loginService = new LoginService(memoryAuthDAO, memoryUserDAO);
        this.listGamesService = new ListGamesService(memoryGameDAO, memoryAuthDAO);
        this.createGameService = new CreateGameService(memoryAuthDAO, memoryGameDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::deleteDB);
        Spark.post("/user", this::registerUser);
        Spark.delete("/session", this::logout);
        Spark.post("/session", this::login);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
        res.type("application/json");
    }

    private Object deleteDB(Request req, Response res) throws ResponseException, DataAccessException {
        clearService.deleteDB();
        res.status(200);
        Map<String, Object> response = new HashMap<>();
        return new Gson().toJson(response);
    }

    private Object logout(Request req, Response res) throws ResponseException, DataAccessException{
        var authToken = new Gson().fromJson(req.headers("Authorization"), String.class);
        Object logoutResponse = logoutService.logout(authToken);
        return new Gson().toJson(logoutResponse);
    }

    private Object login(Request req, Response res) throws ResponseException, DataAccessException{
        var usernameAndPassword = new Gson().fromJson(req.body(), UserData.class);
        Object loginResponse = loginService.login(usernameAndPassword);
        return new Gson().toJson(loginResponse);
    }

    private Object registerUser(Request req, Response res) throws ResponseException, DataAccessException{
        var user = new Gson().fromJson(req.body(), UserData.class);
        Object registerResponse = registerService.addUser(user);
        return new Gson().toJson(registerResponse);
    }

    private Object listGames(Request req, Response res) throws ResponseException, DataAccessException{
        var authToken = new Gson().fromJson(req.headers("Authorization"), String.class);
        Object listGamesResponse = listGamesService.listGames(authToken);
        return new Gson().toJson(listGamesResponse);
    }

    private Object createGame(Request req, Response res) throws ResponseException, DataAccessException{
        var gameNameObject = new Gson().fromJson(req.body(), GameData.class);
        String gameName = gameNameObject.gameName();
        var authToken = new Gson().fromJson(req.headers("Authorization"), String.class);
        Object createGameResponse = createGameService.createGame(authToken,gameName);
        return new Gson().toJson(createGameResponse);
    }}
