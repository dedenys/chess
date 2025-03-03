package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken);
    AuthData createAuth(AuthData auth);
    void clear();
}
