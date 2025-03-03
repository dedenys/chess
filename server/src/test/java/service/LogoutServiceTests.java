package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTests {

    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();

    @BeforeAll
    public static void createUser() {
        RegisterService service = new RegisterService(USER_DAO, AUTH_DAO);
        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        service.register(request);
    }

    @Test
    public void logoutValid() {
        LoginService loginService = new LoginService(USER_DAO, AUTH_DAO);
        LoginRequest loginRequest = new LoginRequest("Bob", "pass123");
        LoginResult loginResult = loginService.login(loginRequest);

        String token = loginResult.authToken();

        LogoutService logoutService = new LogoutService(AUTH_DAO);
        LogoutRequest logoutRequest = new LogoutRequest(token);
        LogoutResult logoutResult = logoutService.logout(logoutRequest);

        assertNull(logoutResult.message());

    }

    @Test
    public void logoutInvalidToken() {
        LoginService loginService = new LoginService(USER_DAO, AUTH_DAO);
        LoginRequest loginRequest = new LoginRequest("Bob", "pass123");
        LoginResult loginResult = loginService.login(loginRequest);

        String token = "123";

        LogoutService logoutService = new LogoutService(AUTH_DAO);
        LogoutRequest logoutRequest = new LogoutRequest(token);

        RequestException exception = assertThrows(RequestException.class, () -> logoutService.logout(logoutRequest));
        assertEquals("unauthorized", exception.getMessage());

    }
}
