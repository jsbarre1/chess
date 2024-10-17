package service;

import chess.ChessPosition;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DeleteDBTests {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private AuthService authService = new AuthService(memoryAuthDAO);
    private GameService gameService = new GameService(memoryGameDAO);
    private UserService userService = new UserService(memoryUserDAO);

    @BeforeEach
    public void setUp(){
        //add things to the data
    }

    @Test
    public void deleteAllAuth(){
        authService.deleteAllAuth();
    }

    @Test
    public void deleteAllGames(){

    }

    @Test
    public void deleteAllUsers(){

    }

    @Test
    public void testDeleteDB(){

    }



}
