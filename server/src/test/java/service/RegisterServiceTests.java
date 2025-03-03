package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTests {

    @Test
    public void registerUserValidRequest() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        RegisterService service = new RegisterService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = service.register(request);
        assertNotNull(result);
        assertNotNull(userDAO.getUser("Bob"));
        assertNotNull(authDAO.getAuth(result.authToken()));
    }
    @Test
    public void registerUserInValidRequest() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        RegisterService service = new RegisterService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", null);

        RequestException exception = assertThrows(RequestException.class, () -> service.register(request));
        assertEquals("bad request", exception.getMessage());
    }
    @Test
    public void registerTakenUsername() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        RegisterService service = new RegisterService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = service.register(request);

        RegisterRequest request2 = new RegisterRequest("Bob", "pass321", "bob2@gmail.com");

        RequestException exception = assertThrows(RequestException.class, () -> service.register(request2));
        assertEquals("already taken", exception.getMessage());
    }
}
