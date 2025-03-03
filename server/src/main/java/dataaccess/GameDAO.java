package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData getGame(int id);
    GameData createGame(GameData game);
    void clear();
}
