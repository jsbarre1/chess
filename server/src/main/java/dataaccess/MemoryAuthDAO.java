package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO{
    final private Collection<AuthData> authDataList = new ArrayList<>();

    public void deleteAllAuths() {
        authDataList.clear();
    }
}
