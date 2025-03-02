package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> authorizations = new HashMap<>();

    public AuthData getAuth(String authToken) {
        AuthData auth = authorizations.get(authToken);
        return auth;
    }
    public AuthData createAuth(AuthData auth) {
        authorizations.put(auth.authToken(), auth);
        return auth;
    }
}
