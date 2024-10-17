package dataaccess;

import model.AuthData;

import java.util.List;

public interface AuthDAO {
    void deleteAllAuths() throws DataAccessException;
}
