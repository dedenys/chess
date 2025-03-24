package service;

import dataaccess.*;
import model.request.CreateGameRequest;
import model.request.ListGamesRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import model.result.ListGamesResult;
import model.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTests {

    private static final GameDAO GAME_DAO = new MemoryGameDAO();
    private static final UserDAO USER_DAO = new MemoryUserDAO();
    private static final AuthDAO AUTH_DAO = new MemoryAuthDAO();
    static String token;


    @BeforeAll
    public static void addGames() {
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
    public void listGamesValidAuth() {
        ListGamesService service = new ListGamesService(GAME_DAO, AUTH_DAO);
        ListGamesRequest request = new ListGamesRequest(token);

        ListGamesResult result = service.listGames(request);

        assertNotNull(result);
    }

    @Test
    public void listGamesInvalidAuth() {
        LogoutService logoutService = new LogoutService(AUTH_DAO);
        LogoutRequest logoutRequest = new LogoutRequest(token);
        logoutService.logout(logoutRequest);

        ListGamesService service = new ListGamesService(GAME_DAO, AUTH_DAO);
        ListGamesRequest request = new ListGamesRequest(token);

        RequestException exception = assertThrows(RequestException.class, () -> service.listGames(request));
        assertEquals("unauthorized", exception.getMessage());
    }
}
