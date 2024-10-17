package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exceptions.ResponseException;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private AuthService authService;
    private GameService gameService;
    private UserService userService;

    public Server(AuthService authService,  GameService gameService, UserService userService) {
        this.authService = authService;
        this.gameService = gameService;
        this.userService = userService;
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
        userService.deleteAllUsers();
        gameService.deleteAllGames();
        res.status(204);
        return "";
    }

    private Object registerUser(Request req, Response res) {
        var user = new Gson().fromJson(req.body(), UserData.class);
        Object response = null;
        try{
            response = userService.addUser(user);
        }catch (ResponseException e){
            res.status(e.StatusCode());
            res.body(e.getMessage());
        }
        return new Gson().toJson(response);
    }
}
