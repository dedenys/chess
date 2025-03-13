package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    DatabaseGameDAO gameDAO = new DatabaseGameDAO();

    @BeforeEach
    public void resetDatabase() {
        gameDAO.clear();
    }

    @Test
    public void createGameBlankSuccessful() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        assertEquals(gameDAO.createGame(game), game);
    }
    @Test
    public void createGameBlankFailure() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, null, chess);

        assertNull(gameDAO.createGame(game));
    }
    @Test
    public void createGameModifiedSuccessful() throws InvalidMoveException {
        ChessGame chess = new ChessGame();
        ChessPosition start = new ChessPosition(2,1);
        ChessPosition end = new ChessPosition(3,1);
        ChessMove m = new ChessMove(start,end, null);

        chess.makeMove(m);

        GameData game = new GameData(1, "bob", null, "myGame", chess);

        assertEquals(gameDAO.createGame(game), game);
    }
    @Test
    public void getGameSuccessful() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(1));
    }
    @Test
    public void getGameFailure() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        gameDAO.createGame(game);

        assertNull(gameDAO.getGame(2));
    }
    @Test
    public void getAllGamesSuccess() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, "bob", null, "myGame", chess);
        ChessGame chess2 = new ChessGame();
        GameData game2 = new GameData(2, null, "john", "game2", chess2);

        gameDAO.createGame(game);
        gameDAO.createGame(game2);

        var result = new ArrayList<GameData>();
        result.add(game);
        result.add(game2);

        assertEquals(gameDAO.getAllGames().size(), result.size());
    }
    @Test
    public void getAllGamesFailure() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, "bob", null, "myGame", chess);
        ChessGame chess2 = new ChessGame();
        GameData game2 = new GameData(2, null, "john", "game2", chess2);

        gameDAO.createGame(game);
        gameDAO.createGame(game2);

        var result = new ArrayList<GameData>();
        result.add(game);
        result.add(game2);

        gameDAO.clear();

        assertTrue(gameDAO.getAllGames().size() == 0);
    }
    @Test
    public void addWhiteUserToGameSuccessful() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        gameDAO.createGame(game);
        gameDAO.addWhiteUserToGame(1, "bob");

        assertEquals(gameDAO.getGame(1).whiteUsername(), "bob");
    }
    @Test
    public void addWhiteUserToGameFailure() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        gameDAO.createGame(game);
        gameDAO.addWhiteUserToGame(1, "bob");

        assertNotEquals(gameDAO.getGame(1).whiteUsername(), "john");
    }
    @Test
    public void addBlackUserToGameSuccessful() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        gameDAO.createGame(game);
        gameDAO.addBlackUserToGame(1, "bob");

        assertEquals(gameDAO.getGame(1).blackUsername(), "bob");
    }
    @Test
    public void addBlackUserToGameFailure() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, null, null, "myGame", chess);

        gameDAO.createGame(game);
        gameDAO.addBlackUserToGame(1, "bob");

        assertNotEquals(gameDAO.getGame(1).blackUsername(), "john");
    }
    @Test
    public void clear() {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, "bob", null, "myGame", chess);
        ChessGame chess2 = new ChessGame();
        GameData game2 = new GameData(2, null, "john", "game2", chess2);

        gameDAO.createGame(game);
        gameDAO.createGame(game2);

        var result = new ArrayList<GameData>();
        result.add(game);
        result.add(game2);

        assertEquals(gameDAO.getAllGames().size(), result.size());

        gameDAO.clear();

        assertTrue(gameDAO.getAllGames().size() == 0);
    }

}
