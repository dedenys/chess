package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;

public class JoinGameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws RequestException {
        String token = joinGameRequest.authToken();
        String teamColor = joinGameRequest.playerColor();
        int id = joinGameRequest.gameID();

        JoinGameResult result;

        if (token != null && teamColor != null && id != 0 && (teamColor.equals("BLACK") || teamColor.equals("WHITE"))) { // check for bad request
            AuthData auth = authDAO.getAuth(token);
            if (auth != null) { // authorize
                String username = auth.username();
                GameData game = gameDAO.getGame(id);
                System.out.println(game);
                if (game != null) {
                    if (teamColor.equals("BLACK") && game.blackUsername() == null) {
                        gameDAO.addBlackUserToGame(id, username);
                        result = new JoinGameResult(null);
                    }
                    else if (teamColor.equals("WHITE") && game.whiteUsername() == null) {
                        gameDAO.addWhiteUserToGame(id, username);
                        result = new JoinGameResult(null);
                    }
                    else {
                        throw  new RequestException("already taken");
                    }
                }
                else {
                    throw new RequestException("invalid game");
                }
            }
            else {
                throw new RequestException("unauthorized");
            }
        }
        else {
            throw new RequestException("bad request");
        }
        return result;
    }
}
