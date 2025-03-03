package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGame(int id);
    GameData createGame(GameData game);
    Collection getAllGames();
    void addWhiteUserToGame(int id, String username);
    void addBlackUserToGame(int id, String username);
    void clear();
}
