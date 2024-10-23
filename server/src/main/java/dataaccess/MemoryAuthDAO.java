package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private Map<String, AuthData> authList;

    public MemoryAuthDAO(){
        this.authList = new HashMap<>();
    }

    public void deleteAllAuths() throws DataAccessException{
        try{
            authList.clear();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData addAuth(String username) throws DataAccessException{
        AuthData authData = new AuthData(username, UUID.randomUUID().toString());
        try{
            authList.put(authData.authToken(), authData);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try{
            return authList.get(authToken);
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        if(getAuth(authToken)==null){
            throw new DataAccessException("Auth Does Not Exist");
        }else {authList.remove(authToken);}

    }

}
