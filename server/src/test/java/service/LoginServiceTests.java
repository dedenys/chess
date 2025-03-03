package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {

    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();


    @BeforeAll
    public static void createUser() {
        RegisterService service = new RegisterService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        service.register(request);
    }

    @Test
    public void loginValid() {
        LoginService service = new LoginService(userDAO, authDAO);
        LoginRequest request = new LoginRequest("Bob", "pass123");
        LoginResult result = service.login(request);

        assertNotNull(result);
        assertNotNull(result.authToken());
        assertNotNull(result.username());
    }

    @Test
    public void loginInvalidPassword() {
        LoginService service = new LoginService(userDAO, authDAO);
        LoginRequest request = new LoginRequest("Bob", "abc");

        RequestException exception = assertThrows(RequestException.class, () -> service.login(request));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    public void loginInvalidUsername() {
        LoginService service = new LoginService(userDAO, authDAO);
        LoginRequest request = new LoginRequest("Jimmy", "abc");

        RequestException exception = assertThrows(RequestException.class, () -> service.login(request));
        assertEquals("not a valid username", exception.getMessage());
    }
}
