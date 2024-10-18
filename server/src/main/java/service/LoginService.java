package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class LoginService {
    private MemoryAuthDAO memoryAuthDAO;
    private MemoryUserDAO memoryUserDAO;
    public LoginService(MemoryAuthDAO memoryAuthDAO, MemoryUserDAO memoryUserDAO){
        this.memoryAuthDAO = memoryAuthDAO;
        this.memoryUserDAO = memoryUserDAO;
    }

    public Object login(UserData usernameAndPassword) throws ResponseException {
        UserData userData = memoryUserDAO.getUser(usernameAndPassword.username());

        if(userData == null){
            throw new ResponseException(401, "Error: unauthorized");
        }

        if(!Objects.equals(usernameAndPassword.password(), userData.password())){
            throw new ResponseException(401, "Error: unauthorized");
        }

        return memoryAuthDAO.addAuth(usernameAndPassword.username());
    }


}
