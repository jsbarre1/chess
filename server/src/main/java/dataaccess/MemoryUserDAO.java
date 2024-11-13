package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    final private Map<String, UserData> userList;

    public MemoryUserDAO(){
        this.userList = new HashMap<>();
    }

    public void deleteAllUsers() throws DataAccessException{
        try{
            userList.clear();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void addUser(UserData userData) throws DataAccessException{
        try{
            userList.put(userData.username(), userData);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    public UserData getUser(String username) throws DataAccessException{

        try{
            return userList.get(username);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

    }
}
