package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    final private Map<String, AuthData> authList;

    public MemoryAuthDAO(){
        this.authList = new HashMap<>();
    }

    public void deleteAllAuths() {
        authList.clear();
    }

    public AuthData addAuth(String authToken, String username){
        AuthData authData = new AuthData(username, authToken);
        authList.put(authToken, authData);
        return authData;
    }

    public AuthData getAuth(String authToken){
        return authList.get(authToken);
    }

    public void deleteAuth(String authToken){
        authList.remove(authToken);
    }

}
