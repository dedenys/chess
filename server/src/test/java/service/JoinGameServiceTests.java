package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTests {

    private static final GameDAO GAME_DAO = new MemoryGameDAO();
    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    static String token;


    @BeforeAll
    public static void createUser() {
        RegisterService service = new RegisterService(USER_DAO, AUTH_DAO);
        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = service.register(request);
        token = result.authToken();

        CreateGameService gameService = new CreateGameService(GAME_DAO, AUTH_DAO);
        CreateGameRequest gameRequest = new CreateGameRequest(token, "Game1");
        CreateGameRequest gameRequest2 = new CreateGameRequest(token, "Game2");
        gameService.createGame(gameRequest);
        gameService.createGame(gameRequest2);

    }

    @Test
    public void joinGameValid() {
        JoinGameService service = new JoinGameService(GAME_DAO, AUTH_DAO);
        JoinGameRequest request = new JoinGameRequest(token, "WHITE", 1);

        JoinGameResult result = service.joinGame(request);

        assertNull(result.message());
    }

    @Test
    public void joinGameInvalidAuth() {
        JoinGameService service = new JoinGameService(GAME_DAO, AUTH_DAO);
        JoinGameRequest request = new JoinGameRequest("123", "BLACK", 1);

        RequestException exception = assertThrows(RequestException.class, () -> service.joinGame(request));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    public void joinGameColorTaken() {
        JoinGameService service = new JoinGameService(GAME_DAO, AUTH_DAO);
        JoinGameRequest request = new JoinGameRequest(token, "WHITE", 1);

        RequestException exception = assertThrows(RequestException.class, () -> service.joinGame(request));
        assertEquals("already taken", exception.getMessage());
    }
}
