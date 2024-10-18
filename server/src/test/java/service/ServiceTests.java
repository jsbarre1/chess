package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.exception.TestException;


public class ServiceTests {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private ClearService clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    private RegisterService registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
    private static UserData testUser;

    @BeforeAll
    public static void init(){
        //test user
        testUser = new UserData("Test", "Testing", "Test@gmail.com");

    }

    @BeforeEach
    public void setUp(){
        //try register
        try {
            registerService.addUser(testUser);
        } catch (ResponseException e) {
            throw new TestException("Register User Failed");
        }
    }

    @Test
    public void testDeleteDB(){
        clearService.deleteDB();
        //get user

        //assert if user is still in DB
    }



}
