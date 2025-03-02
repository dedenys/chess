package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> authorizations = new HashMap<>();

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authorizations.get(authToken);
    }
    public AuthData createAuth(AuthData auth) throws  DataAccessException {
        authorizations.put(auth.authToken(), auth);
        return auth;
    }
}
