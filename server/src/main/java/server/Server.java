package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.UserData;
import service.ClearService;
import service.LogoutService;
import service.RegisterService;
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
;
    public Server() {
        this.clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        this.registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
        this.logoutService = new LogoutService(memoryAuthDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::deleteDB);
        Spark.post("/user", this::registerUser);
        Spark.delete("/session", this::deleteSession);
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

    private Object deleteDB(Request req, Response res) throws ResponseException {
        clearService.deleteDB();
        res.status(200);
        Map<String, Object> response = new HashMap<>();
        return new Gson().toJson(response);
    }

    private Object deleteSession(Request req, Response res) throws ResponseException{
        var authToken = new Gson().fromJson(req.headers("Authorization"), String.class);
        Object logoutResponse = logoutService.logout(authToken);
        return new Gson().toJson(logoutResponse);
    }

    private Object registerUser(Request req, Response res) throws ResponseException{
        var user = new Gson().fromJson(req.body(), UserData.class);
        Object response = registerService.addUser(user);
        return new Gson().toJson(response);
    }
}
