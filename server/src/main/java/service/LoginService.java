package service;

import dataaccess.*;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.UserFunctions;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class LoginService {
    private final AuthFunctions authFunctions;
    private final UserFunctions userFunctions;
    public LoginService(AuthDAO authDAO, UserDAO userDAO){
        this.authFunctions = new AuthFunctions(authDAO);
        this.userFunctions = new UserFunctions(userDAO);
    }

    public AuthData login(UserData usernameAndPassword) throws ResponseException, DataAccessException {
        UserData userData = userFunctions.getUser(usernameAndPassword.username());

        if(userData == null){
            throw new ResponseException(401, "Error: unauthorized");
        }

        if(!BCrypt.checkpw(usernameAndPassword.password(), userData.password())){
            throw new ResponseException(401, "Error: unauthorized");
        }

        return authFunctions.addAuth(usernameAndPassword.username());
    }


}
