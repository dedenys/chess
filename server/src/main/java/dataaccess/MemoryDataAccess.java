package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextId = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authorizations = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
    public UserData createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
        return user;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authorizations.get(authToken);
    }
    public AuthData createAuth(AuthData auth) throws  DataAccessException {
        authorizations.put(auth.authToken(), auth);
        return auth;
    }
}
