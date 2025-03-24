package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    @Test
    public void clearData() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        RegisterService registerService = new RegisterService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = registerService.register(request);
        RegisterRequest request2 = new RegisterRequest("Jimmy", "pass123", "jimmy@gmail.com");
        RegisterResult result2 = registerService.register(request2);

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        assertNull(userDAO.getUser(result.username()));
        assertNull(userDAO.getUser(result2.username()));

    }
}
