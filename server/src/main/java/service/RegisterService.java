package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class RegisterService {
    MemoryUserDAO memoryUserDAO;
    MemoryAuthDAO memoryAuthDAO;


    public RegisterService(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO){
        this.memoryUserDAO = memoryUserDAO;
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public void deleteAllUsers(){
        memoryUserDAO.deleteAllUsers();
    }

    public Object addUser(UserData addUserRequest)throws ResponseException {
        if(addUserRequest.username() == null || addUserRequest.password() == null || addUserRequest.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        if(memoryUserDAO.getUser(addUserRequest.username())==null){
            UserData userData = memoryUserDAO.addUser(addUserRequest.username(), addUserRequest.password(), addUserRequest.email());
            AuthData authData = memoryAuthDAO.addAuth(addUserRequest.username());
            return authData;
        }else {
            throw new ResponseException(403, "Error: already taken");
        }
    }
}
