package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class Services {
    private ClearService clearService;
    private CreateGameService createGameService;
    private  JoinGameService joinGameService;
    private ListGamesService listGamesService;
    private LoginService loginService;
    private LogoutService logoutService;
    private RegisterService registerService;

    public ClearService getClearService() {
        return clearService;
    }

    public CreateGameService getCreateGameService() {
        return createGameService;
    }

    public ListGamesService getListGamesService() {
        return listGamesService;
    }

    public JoinGameService getJoinGameService() {
        return joinGameService;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public RegisterService getRegisterService() {
        return registerService;
    }

    public LogoutService getLogoutService() {
        return logoutService;
    }

    public Services(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        this.registerService = new RegisterService(memoryUserDAO, memoryAuthDAO);
        this.logoutService = new LogoutService(memoryAuthDAO);
        this.loginService = new LoginService(memoryAuthDAO, memoryUserDAO);
        this.listGamesService = new ListGamesService(memoryGameDAO, memoryAuthDAO);
        this.createGameService = new CreateGameService(memoryAuthDAO, memoryGameDAO);
        this.joinGameService = new JoinGameService(memoryGameDAO, memoryAuthDAO);
    }

}
