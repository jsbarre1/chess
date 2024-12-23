package ui;

import chess.*;
import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import websocket.NotificationHandler;
import websocket.WSClient;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class ChessClient implements NotificationHandler {
  private String visitorName=null;
  private final ServerFacade serverFacade;
  private State state=State.SIGNEDOUT;
  private AuthData currentUser;
  private Boolean activeGame=false;
  private ChessGame currGameData;
  private ChessGame.TeamColor currTeamColor;
  private WSClient ws;
  private final String serverUrl;
  private int currGameID;


  public ChessClient(String serverUrl) {

    serverFacade=new ServerFacade(serverUrl);
    this.serverUrl=serverUrl;
  }

  public String eval(String input) {
    try {
      var tokens=input.toLowerCase().split(" ");
      var cmd=tokens[0];
      var params=Arrays.copyOfRange(tokens, 1, tokens.length);
      if (state == State.SIGNEDOUT) {
        return switch (cmd) {
          case "login" -> login(params);
          case "register" -> register(params);
          case "quit" -> "quit";
          case "help" -> help();
          default -> "♕ Welcome to 240 Chess. Type help to get started ♕";
        };
      } else if (state == State.SIGNEDIN && !activeGame) {
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

    } catch (ResponseException | InvalidMoveException ex) {
      return ex.getMessage();
    }
  }


  private String createGame(String... params) throws ResponseException {
    if (params.length == 1) {
      CreateGameRequest request=new CreateGameRequest(params[0]);
      serverFacade.createChessGame(request);
      return "successfully created game";
    }
    throw new ResponseException(400, "Wrong format for create... Expected: <GAMENAME> ");
  }

  private String joinGame(String... params) throws ResponseException {
    if (params.length == 2) {
      int parsedInt;
      try {
        parsedInt=Integer.parseInt(params[0]);
      } catch (NumberFormatException e) {
        return "please input a number for the ID";
      }

      if (!Objects.equals(params[1], "black") && !Objects.equals(params[1], "white")) {
        return "please choose WHITE or BLACK";
      }
      ArrayList<GameData> games=serverFacade.listChessGames();

      if (parsedInt > games.size()) {
        return "enter valid ID";
      }
      if (parsedInt < 1) {
        return "enter valid ID";
      }

      GameData gameData=games.get(parsedInt - 1);
      currGameData=gameData.game();
      currGameID=gameData.gameID();

      try {
        JoinGameRequest request=new JoinGameRequest(params[1].toUpperCase(), gameData.gameID());
        serverFacade.joinGame(request);
        ws=new WSClient(serverUrl, this);

        if (Objects.equals(params[1], "black")) {
          currTeamColor=ChessGame.TeamColor.BLACK;
          ws.joinPlayer(currGameID, currentUser.authToken(), ChessGame.TeamColor.BLACK);
        } else {
          currTeamColor=ChessGame.TeamColor.WHITE;
          ws.joinPlayer(currGameID, currentUser.authToken(), ChessGame.TeamColor.WHITE);

        }
      } catch (ResponseException e) {
        return "That color is full for that game";
      }

      activeGame=true;

      System.out.println(" ");
      return "successfully joined game as " + currTeamColor;
    }
    throw new ResponseException(400, "Wrong format for join... Expected: <ID> [WHITE|BLACK] ");
  }

  private String observeGame(String... params) throws ResponseException {
    if (params.length == 1) {
      int parsedInt;
      try {
        parsedInt=Integer.parseInt(params[0]);
      } catch (NumberFormatException e) {
        return "please input a number for the ID";
      }
      ArrayList<GameData> games=serverFacade.listChessGames();

      if (parsedInt > games.size()) {
        return "enter valid ID";
      }

      if (parsedInt < 1) {
        return "enter valid ID";
      }

      GameData gameData=games.get(parsedInt - 1);
      currGameID=gameData.gameID();
      activeGame=true;
      ws=new WSClient(serverUrl, this);
      ws.joinObserver(currGameID, currentUser.authToken(), null);
      return "observing game: " + parsedInt;
    }
    throw new ResponseException(400, "Wrong format for join... Expected: <ID> [WHITE|BLACK] ");

  }

  private String listGames() throws ResponseException {
    ArrayList<GameData> gameData=serverFacade.listChessGames();
    StringBuilder result=new StringBuilder();
    int i=1;
    for (GameData game : gameData) {
      result.append(i).append(" | ").append("GAMENAME: \"")
              .append(game.gameName()).append("\" | WHITE USER: \"").append(game.whiteUsername())
              .append("\" | BLACK USER: \"").append(game.blackUsername()).append("\"\n");
      i++;
    }

    if (result.isEmpty()) {
      return "no games found";
    }
    return result.toString();
  }

  public String login(String... params) throws ResponseException {
    if (params.length == 2) {
      UserData userData=new UserData(params[0], params[1], null);
      AuthData authData;
      try {
        authData=serverFacade.loginUser(userData);
      } catch (ResponseException e) {
        return "Username or password incorrect";
      }
      state=State.SIGNEDIN;
      visitorName=authData.username();
      currentUser=authData;
      return String.format("You signed in as %s.", visitorName);
    }
    throw new ResponseException(400, "Wrong format for login... Expected: <USERNAME> <PASSWORD>");
  }

  public String logout() throws ResponseException {
    serverFacade.logoutUser();
    state=State.SIGNEDOUT;
    visitorName=null;
    return "Successfully logged out";
  }

  public String register(String... params) throws ResponseException {
    if (params.length == 3) {
      UserData userData=new UserData(params);
      visitorName=params[0];
      try {
        currentUser=serverFacade.registerUser(userData);
      } catch (ResponseException e) {
        return "Username already taken";
      }
      state=State.SIGNEDIN;
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

  public String helpGameplay() {
    return """
            redraw - redraws the board
            mm <> <> - makes a move from one point to the other
            resign - forfeit the game
            highlight - shows the legal moves
            leave - leave the game
            help - with possible commands
            """;
  }

  public String leave() throws ResponseException {
    activeGame=false;
    ws.leave(currGameID, currentUser.authToken());
    return "successfully left game";
  }

  private final Map<Character, Integer> toNumber=Map.of(
          'a', 1,
          'b', 2,
          'c', 3,
          'd', 4,
          'e', 5,
          'f', 6,
          'g', 7,
          'h', 8
  );


  public ChessPiece.PieceType getPieceType(String piece) {
    return switch (piece.toUpperCase()) {
      case "QUEEN" -> ChessPiece.PieceType.QUEEN;
      case "BISHOP" -> ChessPiece.PieceType.BISHOP;
      case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
      case "ROOK" -> ChessPiece.PieceType.ROOK;
      case "PAWN" -> ChessPiece.PieceType.PAWN;
      default -> null;
    };
  }

  private String makeMove(String... params) throws InvalidMoveException, ResponseException {
    if (params.length >= 2) {
      if (!activeGame) {
        return "You are observing not playing";
      }
      ChessPosition start=new ChessPosition(Character.getNumericValue(params[0].charAt(1)),
             9- toNumber.get((params[0].charAt(0))));
      ChessPosition end=new ChessPosition(Character.getNumericValue(params[1].charAt(1)),
              9-toNumber.get(params[1].charAt(0)));

      ChessPiece.PieceType promotionPiece=null;
      if (params.length >= 3) {
        promotionPiece=getPieceType(params[2]);
      }
      ChessMove move=new ChessMove(start, end, promotionPiece);
      ws.makeMove(currGameID, currentUser.authToken(), move);
    } else {
      throw new ResponseException(400, "Expected: <FROM> <TO> <PROMOTION-PIECE> " +
              "(promotion piece only applicable with promotion of pawn)");
    }
    return " ";
  }

  public String resign() throws ResponseException {

    ws.resign(currGameID, currentUser.authToken());
    return "You resigned. Game over.";
  }

  public String highlight(String... params) throws ResponseException {

    if (params.length == 1){
      ChessPosition start = new ChessPosition(Character.
              getNumericValue(params[0].charAt(1)),9 -
              toNumber.get(params[0].charAt(0)));
      if (currGameData.getBoard().getPiece(start) == null){
        return "No piece found at selected position. Try another position.";
      }
      DrawBoard drawBoard = new DrawBoard(currGameData.getBoard());
      drawBoard.printBoard(currTeamColor, currGameData, currGameData.validMoves(start));
      return "Valid moves highlighted in yellow.";
    }

    throw new ResponseException(400, "Expected: <POSITION>");
  }

  public String redraw() {
    DrawBoard drawBoard=new DrawBoard(currGameData.getBoard());
    drawBoard.printBoard(currTeamColor, currGameData, null);
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
    currGameData=loadGameMessage.getGame();
    DrawBoard drawBoard=new DrawBoard(currGameData.getBoard());
    System.out.println(" ");
    drawBoard.printBoard(currTeamColor, currGameData, null);

  }
}
