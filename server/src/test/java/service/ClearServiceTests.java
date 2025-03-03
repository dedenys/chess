package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.ClearResult;
import result.RegisterResult;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    @Test
    public void clearData() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        RegisterService registerService = new RegisterService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        registerService.register(request);
        request = new RegisterRequest("Jimmy", "pass123", "jimmy@gmail.com");
        registerService.register(request);

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        assertEquals(userDAO.getLength(), 0);
        assertEquals(authDAO.getLength(), 0);
        assertEquals(gameDAO.getLength(), 0);

    }
}
