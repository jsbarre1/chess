package server;

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
//        Spark.delete("/db", (request, response) -> this::);
//


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
