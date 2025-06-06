package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.request.*;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;
import model.result.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;

public class LoggedinClient {
    private final String serverUrl;
    private final ServerFacade server;
    public State state = State.LOGGEDIN;
    public boolean isObserving = false;
    public String auth = null;
    public GameData game = null;
    private GameData[] availableGames;
    public static String color;
    public int gameID;


    public LoggedinClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String help() {
        return """
                    - help
                    - logout
                    - create <gamename>
                    - play <gameID> <color>
                    - list
                    - observe <gameID>
                    - quit
                    """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "list" -> list();
                case "logout" -> logout();
                case "create" -> create(params);
                case "play" -> play(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws Exception {
        state = State.PRELOGIN;

        LogoutRequest request = new LogoutRequest(auth);
        server.logout(request);
        return ("Have a nice day!");
    }

    public String play(String... params) throws Exception {
        if (params.length == 2) {
            String id = params[0];
            String colorToBe = params[1].toUpperCase();

            if (colorToBe.equals("WHITE") || colorToBe.equals("BLACK")) {
                if (availableGames == null) {
                    return "List games prior to playing.";
                }

                JoinGameRequest request = new JoinGameRequest(auth, colorToBe, Integer.parseInt(id));

                JoinGameResult r = server.joinGame(request, auth);
                gameID = Integer.parseInt(id);
                isObserving = false;
                state = State.GAME;
                game = availableGames[Integer.parseInt(id)-1];
                color = colorToBe;
                return String.format("You joined game:  %s.", id);

            }
            return "Please enter color as 'black' or 'white'";

        }
        throw new Exception("Expected: <gameID> <color>");
    }

    public String observe(String... params) throws Exception {
        if (params.length == 1) {
            String id = params[0];

            if (availableGames == null) {
                return "List games prior to observing.";
            }

            gameID = Integer.parseInt(id);
            isObserving = true;
            state = State.GAME;
            try {
                game = availableGames[Integer.parseInt(id)-1];
            } catch (Exception e) {
                game = null;
            }

            color = "WHITE";
            return String.format("You are observing game. . .  %s.", id);
        }
        throw new Exception("Expected: <gameID>");
    }

    public String create(String... params) throws Exception {
        if (params.length == 1) {
            String name = params[0];

            CreateGameRequest request = new CreateGameRequest(auth, name);

            CreateGameResult r = server.createGame(request, auth);
            return String.format("You created game:  %s.", name);
        }
        throw new Exception("Expected: <gamename>");
    }

    public String list() throws Exception {
        ListGamesRequest request = new ListGamesRequest(auth);
        Collection<GameData> games = server.listGames(auth);
        availableGames = games.toArray(new GameData[games.size()]);
        var result = new StringBuilder();
        var gson = new Gson();
        int counter = 1;
        for (var game : games) {
            String whiteuser = game.whiteUsername();
            String blackuser = game.blackUsername();
            if (whiteuser == null) {
                whiteuser = "-----";
            }
            if (blackuser == null) {
                blackuser = "-----";
            }
            String thisGame = String.format("%s. GAME NAME: %-15s   BLACK: %-5s    WHITE: %s  \n", counter, game.gameName(), blackuser, whiteuser);
            result.append(thisGame);
            counter += 1;
        }
        return result.toString();
    }
}
