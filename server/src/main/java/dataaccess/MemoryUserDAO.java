package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    final private Map<String, UserData> userList;

    public MemoryUserDAO(){
        this.userList = new HashMap<>();
    }

    public void deleteAllUsers() {
        userList.clear();
    }

    public UserData addUser(String username, String password, String email){
        UserData userData = new UserData(username,password,email);
        userList.put(username, userData);
        return userData;
    }

    public UserData getUser(String username){
        return userList.get(username);
    }
}
