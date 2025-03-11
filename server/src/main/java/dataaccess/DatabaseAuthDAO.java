package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class DatabaseAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authorizations = new HashMap<>();

    public AuthData getAuth(String authToken) {
        AuthData auth = authorizations.get(authToken);
        return auth;
    }
    public AuthData createAuth(AuthData auth) {
        authorizations.put(auth.authToken(), auth);
        return auth;
    }
    public void removeAuth(String authToken) {
        authorizations.remove(authToken);
        System.out.println(authorizations);
    }
    public void clear() {
        authorizations.clear();
    }
    public int getLength() {
        return authorizations.size();
    }
}
