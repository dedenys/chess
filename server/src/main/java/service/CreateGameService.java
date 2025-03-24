package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;

public class CreateGameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws RequestException {
        String token = createGameRequest.authToken();
        String gameName = createGameRequest.gameName();

        if (token != null && gameName != null) { // check for bad request
            if (authDAO.getAuth(token) != null) { // authorize
                GameData game = new GameData(0, null, null, gameName, new ChessGame());
                GameData newGame = gameDAO.createGame(game);
                return new CreateGameResult(newGame.gameID(), null);

            }
            else {
                throw new RequestException("unauthorized");
            }
        }
        else {
            throw new RequestException("bad request");
        }
    }
}
