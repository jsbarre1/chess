package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.GameData;
import model.UserData;
import models.JoinGameObject;
import models.ListGamesResult;
import service.*;
import spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    private final Services services;
    public Server() {
        services = new Services(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        Spark.exception(DataAccessException.class, this::exceptionHandlerDataAccess);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
        res.type("application/json");
    }

    private void exceptionHandlerDataAccess(DataAccessException ex, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
        res.type("application/json");
    }

    private Object deleteDB(Request req, Response res) throws ResponseException, DataAccessException {
        services.getClearService().deleteDB();
        res.status(200);
        Map<String, Object> response = new HashMap<>();
        return new Gson().toJson(response);
    }

    private Object logout(Request req, Response res) throws ResponseException, DataAccessException{
        String authToken = parseAuthData(req);
        Object logoutResponse = services.getLogoutService().logout(authToken);
        return new Gson().toJson(logoutResponse);
    }

    private Object login(Request req, Response res) throws ResponseException, DataAccessException{
        var usernameAndPassword = new Gson().fromJson(req.body(), UserData.class);
        Object loginResponse = services.getLoginService().login(usernameAndPassword);
        return new Gson().toJson(loginResponse);
    }

    private Object registerUser(Request req, Response res) throws ResponseException, DataAccessException{

        var user = new Gson().fromJson(req.body(), UserData.class);
        Object registerResponse = services.getRegisterService().addUser(user);
        return new Gson().toJson(registerResponse);
    }

    private Object listGames(Request req, Response res) throws ResponseException, DataAccessException{
        String authToken = parseAuthData(req);
        ArrayList<GameData> listGamesResponse = services.getListGamesService().listGames(authToken);
        ListGamesResult listGamesResult = new ListGamesResult(listGamesResponse);
        return new Gson().toJson(listGamesResult);
    }

    private Object joinGame(Request req, Response res) throws ResponseException, DataAccessException{
        String authToken = parseAuthData(req);
        var colorAndID = new Gson().fromJson(req.body(), JoinGameObject.class);

        Object joinGameResponse = services.getJoinGameService().joinGame(authToken, colorAndID);
        return new Gson().toJson(joinGameResponse);
    }

    private Object createGame(Request req, Response res) throws ResponseException, DataAccessException{
        var gameNameObject = new Gson().fromJson(req.body(), GameData.class);
        String gameName = gameNameObject.gameName();
        String authToken = parseAuthData(req);
        Object createGameResponse = services.getCreateGameService().createGame(authToken,gameName);
        return new Gson().toJson(createGameResponse);
    }

    private String parseAuthData(Request req) throws ResponseException{
        try{
            return new Gson().fromJson(req.headers("Authorization"), String.class);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

}
