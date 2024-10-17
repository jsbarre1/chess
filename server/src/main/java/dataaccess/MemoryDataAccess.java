package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryDataAccess{
    private MDAAuth mdaAuth = new MDAAuth();
    private MDAUser mdaUser = new MDAUser();
    private MDAGame mdaGame = new MDAGame();
}
