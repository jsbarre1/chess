package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    final private Map<String, UserData> userDataList;

    public MemoryUserDAO(){
        this.userDataList = new HashMap<>();
    }

    public void deleteAllUsers() {
        userDataList.clear();
    }

    public UserData addUser(String username, String password, String email){
        UserData userData = new UserData(username,password,email);
        userDataList.put(username, userData);
        return userData;
    }

    public UserData getUser(String username){
        if(userDataList.containsKey(username)){
            return userDataList.get(username);
        }
        return null;
    }
}
