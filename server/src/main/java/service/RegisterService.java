package service;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.UserFunctions;
import model.AuthData;
import model.UserData;

public class RegisterService {
    private UserFunctions userFunctions;
    private AuthFunctions authFunctions;



    public RegisterService(UserDAO userDAO, AuthDAO authDAO){
        this.userFunctions = new UserFunctions(userDAO);
        this.authFunctions = new AuthFunctions(authDAO);
    }

    public AuthData addUser(UserData addUserRequest)throws ResponseException, DataAccessException {
        if(addUserRequest.username() == null || addUserRequest.password() == null || addUserRequest.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        if(userFunctions.getUser(addUserRequest.username())==null){
            String hashedPassword = BCrypt.hashpw(addUserRequest.password(), BCrypt.gensalt());
            UserData crypticUser = new UserData(addUserRequest.username(), hashedPassword, addUserRequest.email());
            userFunctions.addUser(crypticUser);
            return authFunctions.addAuth(addUserRequest.username());
        }else {
            throw new ResponseException(403, "Error: already taken");
        }
    }
}
