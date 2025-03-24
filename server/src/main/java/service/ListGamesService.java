package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import model.request.ListGamesRequest;
import model.result.ListGamesResult;

import java.util.Collection;

public class ListGamesService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws RequestException {
        String token = listGamesRequest.authToken();

        ListGamesResult result;

        if (authDAO.getAuth(token) != null) {
            Collection<GameData> games = gameDAO.getAllGames();
            result = new ListGamesResult(games, null);
            return result;
        }
        else {
            throw new RequestException("unauthorized");
        }
    }
}
