package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGame(int id);
    GameData createGame(GameData game);
    Collection getAllGames();
    void clear();
}
