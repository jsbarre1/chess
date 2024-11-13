package ui;

import exceptions.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
        private String visitorName = null;
        private final ServerFacade server;
        private final String serverUrl;
        private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
            server = new ServerFacade(serverUrl);
            this.serverUrl = serverUrl;
        }

        public String eval(String input) {
            try {
                var tokens = input.toLowerCase().split(" ");
                //var cmd = (tokens.length > 0) ? tokens[0] : "help";
                var cmd = tokens[0];
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                return switch (cmd) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    case "help" -> help();
                    default -> help();
                };
            } catch (ResponseException ex) {
                return ex.getMessage();
            }
        }

        public String login(String... params) throws ResponseException {
            if (params.length >= 1) {
                state = State.SIGNEDIN;
                visitorName = String.join("-", params);
                return String.format("You signed in as %s.", visitorName);
            }
            throw new ResponseException(400, "Expected: <yourname>");
        }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
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
                quit - playing chess
                help - with possible commands
                """;
        }

        private void assertSignedIn() throws ResponseException {
            if (state == State.SIGNEDOUT) {
                throw new ResponseException(400, "You must sign in");
            }
        }
}
