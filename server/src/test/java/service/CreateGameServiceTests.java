package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTests {

    private static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();
    static String token;


    @BeforeAll
    public static void createUser() {
        RegisterService service = new RegisterService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = service.register(request);
        token = result.authToken();
    }

    @Test
    public void createGameValidAuth() {
        CreateGameService service = new CreateGameService(gameDAO, authDAO);
        CreateGameRequest request = new CreateGameRequest(token, "mygame");
        CreateGameResult result = service.createGame(request);

        assertNotNull(gameDAO.getGame(result.gameID()));
    }

    @Test
    public void createGameInvalidAuth() {
        CreateGameService service = new CreateGameService(gameDAO, authDAO);
        CreateGameRequest request = new CreateGameRequest("123", "mygame");

        RequestException exception = assertThrows(RequestException.class, () -> service.createGame(request));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    public void createGameBadRequest() {
        CreateGameService service = new CreateGameService(gameDAO, authDAO);
        CreateGameRequest request = new CreateGameRequest("123", null);

        RequestException exception = assertThrows(RequestException.class, () -> service.createGame(request));
        assertEquals("bad request", exception.getMessage());
    }
    }
