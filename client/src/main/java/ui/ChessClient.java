package ui;

import chess.ChessGame;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import websocket.NotificationHandler;
import websocket.WSClient;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class  ChessClient implements NotificationHandler {
        private String visitorName = null;
        private final ServerFacade serverFacade;
        private State state = State.SIGNEDOUT;
        private AuthData currentUser;
        private Boolean activeGame = false;
        private GameData currGameData;
        private ChessGame.TeamColor currTeamColor;
        private WSClient ws;
        private String serverUrl;
        private int currGameID;



    public ChessClient(String serverUrl) {

        serverFacade= new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
                }else if(state == State.SIGNEDIN && !activeGame){
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
                } else {
                    return switch (cmd) {
                        case "help" -> helpGameplay();
                        case "redraw" -> redraw();
                        case "mm" -> makeMove(params);
                        case "resign" -> resign();
                        case "highlight" -> highlight(params);
                        case "leave" -> leave();
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
            CreateGameResponse response = serverFacade.createChessGame(request);
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
            ArrayList<GameData> games = serverFacade.listChessGames();

            if(parsedInt > games.size()){
                return "enter valid ID";
            }
            if(parsedInt < 1){
                return "enter valid ID";
            }

            GameData gameData = games.get(parsedInt-1);
            currGameData= gameData;
            currGameID = gameData.gameID();

            try{
                JoinGameRequest request = new JoinGameRequest(params[1].toUpperCase(), gameData.gameID());
                serverFacade.joinGame(request);
                ws = new WSClient(serverUrl, this);

                if(Objects.equals(params[1], "black")){
                    currTeamColor = ChessGame.TeamColor.BLACK;
                    ws.joinPlayer(currGameID, currentUser.authToken(), ChessGame.TeamColor.BLACK);
                }else{
                    currTeamColor = ChessGame.TeamColor.WHITE;
                    ws.joinPlayer(currGameID, currentUser.authToken(), ChessGame.TeamColor.WHITE);

                }
            } catch (ResponseException e) {
                return "That color is full for that game";
            }

            activeGame = true;

            DrawBoard drawBoard = new DrawBoard(gameData.game().getBoard());
            drawBoard.printBoard(currTeamColor, gameData.game(),null);

            return "successfully joined game as " + currTeamColor;
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
            ArrayList<GameData> games = serverFacade.listChessGames();

            if(parsedInt > games.size()){
                return "enter valid ID";
            }

            if(parsedInt < 1){
                return "enter valid ID";
            }

            GameData gameData = games.get(parsedInt-1);

            DrawBoard drawBoard = new DrawBoard(gameData.game().getBoard());
            drawBoard.printBoards();
            return "observing game: " + parsedInt;
        }
        throw new ResponseException(400, "Wrong format for join... Expected: <ID> [WHITE|BLACK] ");

    }

    private String listGames() throws ResponseException {
        ArrayList<GameData> gameData = serverFacade.listChessGames();
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
                    authData = serverFacade.loginUser(userData);
                }catch (ResponseException e){
                    return "Username or password incorrect";
                }
                state = State.SIGNEDIN;
                visitorName = authData.username();
                currentUser = authData;
                return String.format("You signed in as %s.", visitorName);
            }
            throw new ResponseException(400, "Wrong format for login... Expected: <USERNAME> <PASSWORD>");
    }

    public String logout() throws ResponseException {
        serverFacade.logoutUser();
        state = State.SIGNEDOUT;
        visitorName = null;
        return "Successfully logged out";
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData userData = new UserData(params);
            visitorName = params[0];
            try {
                currentUser = serverFacade.registerUser(userData);
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

    public String helpGameplay(){
        return """
                redraw - redraws the board
                mm <> <> - makes a move from one point to the other
                resign - forfeit the game
                highlight - shows the legal moves
                leave - leave the game
                help - with possible commands
                """;
    }
    public String leave(){
        activeGame = false;
        return "successfully left game";
    }
    public String makeMove(String... params){
        return "move";
    }
    public String resign(){
        return "resign";
    }
    public String highlight(String... params){
        return "highlight";
    }
    public String redraw(){
        DrawBoard drawBoard = new DrawBoard(currGameData.game().getBoard());
        drawBoard.printBoard(currTeamColor, currGameData.game(), null);
        return "current board drawn";
    }

    @Override
    public void notify(ServerMessage message) {
        if (message == null) {
            System.out.println(SET_TEXT_COLOR_RED + "Received null message");
            return;
        }

        switch (message.getServerMessageType()) {
            case ERROR -> System.out.println(SET_TEXT_COLOR_RED + message.getMessage());
            case NOTIFICATION -> System.out.println(SET_TEXT_COLOR_YELLOW + message.getMessage());
            default -> System.out.println(SET_TEXT_COLOR_YELLOW + "Unknown message type: " + message.getMessage());
        }
    }

    @Override
    public void updateGame(LoadGameMessage loadGameMessage) {

    }
}
