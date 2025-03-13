package dataaccess;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    DatabaseUserDAO userDAO = new DatabaseUserDAO();
    DatabaseAuthDAO authDAO = new DatabaseAuthDAO();

    @BeforeEach
    public void resetDatabases() {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    public void createAuthSuccessful() {
        AuthData auth  = new AuthData("12345", "bob");

        assertEquals(authDAO.createAuth(auth), auth);
    }
    @Test
    public void createAuthFailure() {
        AuthData auth  = new AuthData(null, "bob");

        assertNull(authDAO.createAuth(auth));
    }
    @Test
    public void getAuthSuccessful() {
        AuthData auth  = new AuthData("12345", "bob");
        authDAO.createAuth(auth);

        assertEquals(authDAO.getAuth("12345"), auth);
    }
    @Test
    public void getAuthFailure() {
        AuthData auth  = new AuthData("12345", "bob");

        assertNull(authDAO.getAuth("123"));
    }
    @Test
    public void removeAuthSuccessful() {
        AuthData auth  = new AuthData("12345", "bob");
        authDAO.createAuth(auth);

        assertEquals(authDAO.getAuth("12345"), auth);

        authDAO.removeAuth("12345");

        assertNull(authDAO.getAuth("12345"));
    }
    @Test
    public void removeAuthFailure() {
        AuthData auth  = new AuthData("12345", "bob");
        authDAO.createAuth(auth);

        assertEquals(authDAO.getAuth("12345"), auth);

        authDAO.removeAuth("123");

        assertNotNull(authDAO.getAuth("12345"));
    }
    public void clear() {
        AuthData auth  = new AuthData("12345", "bob");
        AuthData auth2  = new AuthData("token1", "john");
        authDAO.createAuth(auth);
        authDAO.createAuth(auth2);

        assertEquals(authDAO.getAuth("12345"), auth);
        assertEquals(authDAO.getAuth("token1"), auth2);

        authDAO.clear();

        assertNull(authDAO.getAuth("12345"));
        assertNull(authDAO.getAuth("token1"));
    }

}
