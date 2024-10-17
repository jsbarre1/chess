import chess.*;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        var authService = new AuthService(memoryAuthDAO);
        var gameService = new GameService(memoryGameDAO);
        var userService = new UserService(memoryUserDAO);

        var chessServer = new Server(authService, gameService, userService);

        chessServer.run(8080);
    }
}