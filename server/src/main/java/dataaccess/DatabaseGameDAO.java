package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class DatabaseGameDAO implements GameDAO {
    final private HashMap<String, GameData> games = new HashMap<>();
    int id = 1;

    public GameData getGame(int id) {
        String newId = String.valueOf(id);
        return games.get(newId);
    }

    public GameData createGame(GameData game) {
        String newId = String.valueOf(id);
        GameData newGame = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(newId, newGame);
        id++;
        return newGame;
    }

    public Collection<GameData> getAllGames() {
        return games.values();
    }

    public void addWhiteUserToGame(int id, String username) {
        String newId = String.valueOf(id);
        GameData oldData = games.get(newId);
        GameData newData = new GameData(oldData.gameID(), username, oldData.blackUsername(), oldData.gameName(), oldData.game());
        games.put(newId, newData);
    }

    public void addBlackUserToGame(int id, String username) {
        String newId = String.valueOf(id);
        GameData oldData = games.get(newId);
        GameData newData = new GameData(oldData.gameID(), oldData.whiteUsername(), username, oldData.gameName(), oldData.game());
        games.put(newId, newData);
    }

    public void clear() {
        games.clear();
    }
    public int getLength() {
        return games.size();
    }
}
