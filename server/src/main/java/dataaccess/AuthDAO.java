package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException;
    AuthData createAuth(AuthData auth) throws  DataAccessException;
}
