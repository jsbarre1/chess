package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO{
    final private Collection<AuthData> userDataList = new ArrayList<>();

    public void deleteAllUsers() {
        userDataList.clear();
    }
}
