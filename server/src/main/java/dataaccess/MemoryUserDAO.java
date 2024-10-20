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

    public void addUser(UserData userData){
        userList.put(userData.username(), userData);
    }

    public UserData getUser(String username){
        return userList.get(username);
    }
}
