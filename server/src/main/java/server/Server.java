package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.RegisterService;
import spark.*;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private AuthService authService;
    private GameService gameService;
    private RegisterService registerService;

    public Server() {
        this.authService = new AuthService(memoryAuthDAO);
        this.gameService = new GameService(memoryGameDAO);
        this.registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::deleteDB);
        Spark.post("/user", this::registerUser);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object deleteDB(Request req, Response res) throws DataAccessException {
        authService.deleteAllAuth();
        registerService.deleteAllUsers();
        gameService.deleteAllGames();
        res.status(204);
        return "";
    }

    private Object registerUser(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), UserData.class);
        Object response = null;
        try{
            response = registerService.addUser(user);
        }catch (ResponseException e){
            res.status(e.StatusCode());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new Gson().toJson(errorResponse);
        }
        return new Gson().toJson(response);
    }
}
