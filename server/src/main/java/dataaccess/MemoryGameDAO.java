package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<String, GameData> games = new HashMap<>();

    public void clear() {
        games.clear();
    }
}
