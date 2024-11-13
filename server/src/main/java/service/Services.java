package service;

import dataaccess.*;

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

    public Services(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.logoutService = new LogoutService(authDAO);
        this.loginService = new LoginService(authDAO, userDAO);
        this.listGamesService = new ListGamesService(gameDAO, authDAO);
        this.createGameService = new CreateGameService(authDAO, gameDAO);
        this.joinGameService = new JoinGameService(gameDAO, authDAO);
    }

}
