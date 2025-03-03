package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTests {

    private static final GameDAO gameDAO = new MemoryGameDAO();
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();
    static String token;


    @BeforeAll
    public static void createUser() {
        RegisterService service = new RegisterService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest("Bob", "pass123", "bob@gmail.com");
        RegisterResult result = service.register(request);
        token = result.authToken();

        CreateGameService gameService = new CreateGameService(gameDAO, authDAO);
        CreateGameRequest gameRequest = new CreateGameRequest(token, "Game1");
        CreateGameRequest gameRequest2 = new CreateGameRequest(token, "Game2");
        gameService.createGame(gameRequest);
        gameService.createGame(gameRequest2);

    }

    @Test
    public void listGamesValidAuth() {
        ListGamesService service = new ListGamesService(gameDAO, authDAO);
        ListGamesRequest request = new ListGamesRequest(token);

        ListGamesResult result = service.listGames(request);

        assertNotNull(result);
    }

    @Test
    public void listGamesInvalidAuth() {
        LogoutService logoutService = new LogoutService(authDAO);
        LogoutRequest logoutRequest = new LogoutRequest(token);
        logoutService.logout(logoutRequest);

        ListGamesService service = new ListGamesService(gameDAO, authDAO);
        ListGamesRequest request = new ListGamesRequest(token);

        RequestException exception = assertThrows(RequestException.class, () -> service.listGames(request));
        assertEquals("unauthorized", exception.getMessage());
    }
}
