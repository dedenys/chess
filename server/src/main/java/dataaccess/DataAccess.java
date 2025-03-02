package dataaccess;

import model.GameData;
import model.AuthData;
import model.UserData;
import java.util.Collection;

public interface DataAccess {
    UserData getUser(String username) throws DataAccessException;
    UserData createUser(UserData user) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    AuthData createAuth(AuthData auth) throws  DataAccessException;
}
