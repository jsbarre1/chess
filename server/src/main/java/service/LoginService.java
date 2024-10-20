package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.UserFunctions;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class LoginService {
    private AuthFunctions authFunctions;
    private UserFunctions userFunctions;
    public LoginService(MemoryAuthDAO memoryAuthDAO, MemoryUserDAO memoryUserDAO){
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
        this.userFunctions = new UserFunctions(memoryUserDAO);
    }

    public AuthData login(UserData usernameAndPassword) throws ResponseException, DataAccessException {
        UserData userData = userFunctions.getUser(usernameAndPassword.username());

        if(userData == null){
            throw new ResponseException(401, "Error: unauthorized");
        }

        if(!Objects.equals(usernameAndPassword.password(), userData.password())){
            throw new ResponseException(401, "Error: unauthorized");
        }

        return authFunctions.addAuth(usernameAndPassword.username());
    }


}
