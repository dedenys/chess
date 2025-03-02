package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void registerUserValidRequest() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

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
        UserService service = new UserService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", null);
        RegisterResult result = service.register(request);

        RegisterResult expectedResult = new RegisterResult(null, null, "Error: bad request");
        assertEquals(result, expectedResult);
        assertNull(userDAO.getUser("Bob"));
        assertNull(authDAO.getAuth(result.authToken()));
    }
    @Test
    public void registerTakenUsername() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = service.register(request);

        request = new RegisterRequest("Bob", "pass321", "bob2@gmail.com");
        result = service.register(request);

        RegisterResult expectedResult = new RegisterResult(null, null, "Error: already taken");
        assertEquals(result, expectedResult);
    }
}
