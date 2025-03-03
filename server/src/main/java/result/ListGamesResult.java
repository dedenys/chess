package result;

import model.GameData;

import java.util.Collection;
import java.util.Vector;

public record ListGamesResult(Collection<GameData> games, String message) {
}
