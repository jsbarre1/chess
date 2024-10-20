package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import functions.AuthFunctions;
import functions.UserFunctions;
import model.UserData;

public class RegisterService {
    UserFunctions userFunctions;
    AuthFunctions authFunctions;


    public RegisterService(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO){
        this.userFunctions = new UserFunctions(memoryUserDAO);
        this.authFunctions = new AuthFunctions(memoryAuthDAO);
    }

    public Object addUser(UserData addUserRequest)throws ResponseException {
        if(addUserRequest.username() == null || addUserRequest.password() == null || addUserRequest.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        if(userFunctions.getUser(addUserRequest.username())==null){
            userFunctions.addUser(addUserRequest);
            return authFunctions.addAuth(addUserRequest.username());
        }else {
            throw new ResponseException(403, "Error: already taken");
        }
    }
}
