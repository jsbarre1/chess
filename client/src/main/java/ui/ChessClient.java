package ui;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
                var cmd = tokens[0];
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                if(state == State.SIGNEDOUT){
                    return switch (cmd) {
                        case "login" -> login(params);
                        case "register" -> register(params);
                        case "quit" -> "quit";
                        case "help" -> help();
                        default -> "♕ Welcome to 240 Chess. Type help to get started ♕";
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
        if (params.length == 1) {
            CreateGameRequest request = new CreateGameRequest(params[0]);
            CreateGameResponse response = server.createChessGame(request);
            return "successfully created game";
        }
        throw new ResponseException(400, "Wrong format for create... Expected: <GAMENAME> ");
    }

    private String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            int parsedInt;
            try {
                parsedInt = Integer.parseInt(params[0]);
            }catch (NumberFormatException e ){
                return "please input a number for the ID";
            }

            if(!Objects.equals(params[1], "black") && !Objects.equals(params[1], "white")){
                return "please choose WHITE or BLACK";
            }
            ArrayList<GameData> games = server.listChessGames();

            if(parsedInt > games.size()){
                return "enter valid ID";
            }
            if(parsedInt < 1){
                return "enter valid ID";
            }

            GameData gameData = games.get(parsedInt-1);

            try{
                JoinGameRequest request = new JoinGameRequest(params[1].toUpperCase(), gameData.gameID());
                server.joinGame(request);
            } catch (ResponseException e) {
                return "That color is full for that game";
            }

            DrawBoard drawBoard = new DrawBoard(gameData.game().getBoard());
            drawBoard.printBoard();


            return "successfully joined game";
        }
        throw new ResponseException(400, "Wrong format for join... Expected: <ID> [WHITE|BLACK] ");

    }

    private String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            int parsedInt;
            try {
                parsedInt = Integer.parseInt(params[0]);
            }catch (NumberFormatException e ){
                return "please input a number for the ID";
            }
            ArrayList<GameData> games = server.listChessGames();

            if(parsedInt > games.size()){
                return "enter valid ID";
            }

            if(parsedInt < 1){
                return "enter valid ID";
            }

            GameData gameData = games.get(parsedInt-1);

            DrawBoard drawBoard = new DrawBoard(gameData.game().getBoard());
            drawBoard.printBoard();

            return "observing game: " + parsedInt;
        }
        throw new ResponseException(400, "Wrong format for join... Expected: <ID> [WHITE|BLACK] ");

    }

    private String listGames() throws ResponseException {
        ArrayList<GameData> gameData = server.listChessGames();
        StringBuilder result = new StringBuilder();
        int i = 1;
        for (GameData game : gameData) {
            result.append(i).append(" | ").append("GAMENAME: \"")
                    .append(game.gameName()).append("\" | WHITE USER: \"").append(game.whiteUsername())
                    .append("\" | BLACK USER: \"").append(game.blackUsername()).append("\"\n");
            i++;
        }

        if (result.isEmpty()){
            return "no games found";
        }
        return result.toString();
    }

    public String login(String... params) throws ResponseException {
            if (params.length == 2) {
                UserData userData = new UserData(params[0], params[1], null);
                AuthData authData;
                try {
                    authData = server.loginUser(userData);
                }catch (ResponseException e){
                    return "Username or password incorrect";
                }
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
            try {
                server.registerUser(userData);
            }catch (ResponseException e){
                return "Username already taken";
            }
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
