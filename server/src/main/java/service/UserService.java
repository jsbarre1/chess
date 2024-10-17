package service;

import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.UserData;

import java.util.Map;

public class UserService {
    MemoryUserDAO memoryUserDAO;

    public UserService(MemoryUserDAO memoryUserDAO){
        this.memoryUserDAO = memoryUserDAO;
    }


    public void deleteAllUsers(){
        memoryUserDAO.deleteAllUsers();
    }

    public Object addUser(UserData userData)throws ResponseException {
        if(userData.username() == null || userData.password() == null || userData.email() == null){
            throw new ResponseException(400, "Error bad request");
        }
        if(memoryUserDAO.getUser(userData.username())==null){
            UserData responseUserData = memoryUserDAO.addUser(userData.username(), userData.password(), userData.email());
            return responseUserData;
        }

        return null;
    }
}
