package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.exception.TestException;
import passoff.model.TestResult;
import spark.Response;

import java.util.HashMap;
import java.util.Locale;


public class ServiceTests {
    private MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private ClearService clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    private RegisterService registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
    private LoginService loginService = new LoginService(memoryAuthDAO, memoryUserDAO);
    private LogoutService logoutService = new LogoutService(memoryAuthDAO);
    private static UserData testUser;
    private AuthData authData;
    private DataAccessException

    @BeforeAll
    public static void init(){
        //test user
        testUser = new UserData("Test", "Testing", "Test@gmail.com");

    }

    @BeforeEach
    public void setUp() throws ResponseException{
        authData = registerService.addUser(testUser);
    }

    @Test
    @DisplayName("Logout Test")
    public void logoutUser() throws ResponseException{
        Assertions.assertEquals(logoutService.logout(authData.authToken()), new HashMap<>());
    }

    @Test
    @DisplayName("Logout Bad AuthToken")
    public void logoutFails(){
        try {
            logoutService.logout("123");
        } catch (DataAccessException | ResponseException e) {
            Assertions.assertEquals(e, );
        }

    }




}
