package ui;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import response.CreateGameResponse;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;

public class ChessClient {
        private String visitorName = null;
        private final ServerFacade server;
        private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
            server = new ServerFacade(serverUrl);
        }

        public String eval(String input) {
            try {
                var tokens = input.toLowerCase().split(" ");
                //var cmd = (tokens.length > 0) ? tokens[0] : "help";
                var cmd = tokens[0];
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                if(state == State.SIGNEDOUT){
                    return switch (cmd) {
                        case "login" -> login(params);
                        case "register" -> register(params);
                        case "quit" -> "quit";
                        case "help" -> help();
                        default -> "♕ Welcome to 240 Chess. Type help to get started ♕\"";
                    };
                }else{
                    return switch (cmd) {
                        case "logout" -> logout();
                        case "list" -> listGames();
                        case "observe" -> observeGame(params);
                        case "join" -> joinGame(params);
                        case "create" -> createGame(params);
                        case "quit" -> "quit";
                        case "help" -> help();
                        default -> "♕ Type help for commands ♕\"";
                    };
                }

            } catch (ResponseException ex) {
                return ex.getMessage();
            }
        }

    private String createGame(String... params) throws ResponseException {
        System.out.println(Arrays.toString(params));
        if (params.length == 1) {
            CreateGameRequest request = new CreateGameRequest(params[0]);
            CreateGameResponse response = server.createChessGame(request);
            return "successfully created game";
        }
        throw new ResponseException(400, "Wrong format for create... Expected: <GAMENAME> ");
    }

    private String joinGame(String... params) {
        System.out.println("NOT IMPLEMENTED");
        return null;
    }

    private String observeGame(String... params) {
        System.out.println("NOT IMPLEMENTED");
        return null;
    }

    private String listGames() throws ResponseException {
        ArrayList<GameData> gameData = server.listChessGames();
        StringBuilder result = new StringBuilder();
        int i = 1;
        for (GameData game : gameData) {
            result.append("Name of Game: ").append(game.gameName()).append(" ID: ").append(i).append("\n");
            i++;
        }

        if (result.isEmpty()){
            return "no games found";
        }
        return result.toString();
    }

    public String login(String... params) throws ResponseException {
        System.out.println(Arrays.toString(params));
            if (params.length == 2) {
                UserData userData = new UserData(params[0], params[1], null);
                AuthData authData = server.loginUser(userData);
                state = State.SIGNEDIN;
                visitorName = authData.username();
                return String.format("You signed in as %s.", visitorName);
            }
            throw new ResponseException(400, "Wrong format for login... Expected: <USERNAME> <PASSWORD>");
    }

    public String logout() throws ResponseException {
        server.logoutUser();
        state = State.SIGNEDOUT;
        visitorName = null;
        return "Successfully logged out";
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData userData = new UserData(params);
            visitorName = params[0];
            server.registerUser(userData);
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Wrong format for register... Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }




    public String help() {
            if (state == State.SIGNEDOUT) {
                return """
                    register <USERNAME> <PASSWORD> <EMAIL> - TO CREATE AN ACCOUNT
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
            }
            return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    public void printPrompt() {
        System.out.print("[" + state + "]>>> ");
    }

    public void clearData() throws ResponseException {
        System.out.println("initial clearing all data");
        server.clearData();
    }
}
