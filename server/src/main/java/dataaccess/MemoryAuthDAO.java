package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private Map<String, AuthData> authList;

    public MemoryAuthDAO(){
        this.authList = new HashMap<>();
    }

    public void deleteAllAuths() {
        authList.clear();
    }

    public AuthData addAuth(String username){
        AuthData authData = new AuthData(username, UUID.randomUUID().toString());
        authList.put(authData.authToken(), authData);
        return authData;
    }

    public AuthData getAuth(String authToken){
        return authList.get(authToken);
    }

    public void deleteAuth(String authToken){
        authList.remove(authToken);
    }

}
