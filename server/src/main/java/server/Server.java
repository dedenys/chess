package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import model.request.*;
import model.result.*;
import service.*;
import spark.*;

import java.util.Objects;

public class Server {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        userDAO = new DatabaseUserDAO();
        authDAO = new DatabaseAuthDAO();
        gameDAO = new DatabaseGameDAO();

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object joinGame(Request req, Response res) {
        String authToken = req.headers("authorization");

        JoinGameNoAuth body = new Gson().fromJson(req.body(), JoinGameNoAuth.class);

        JoinGameRequest request = new JoinGameRequest(authToken, body.playerColor(), body.gameID());

        JoinGameResult result;

        try {
            JoinGameService service = new JoinGameService(gameDAO, authDAO);
            result = service.joinGame(request);
        }
        catch(RequestException e) {
            if (Objects.equals(e.getMessage(), "bad request")) {
                res.status(400);
                result = new JoinGameResult("Error: bad request");
            }
            else if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                result = new JoinGameResult("Error: unauthorized");
            }
            else if (Objects.equals(e.getMessage(), "already taken")) {
                res.status(403);
                result = new JoinGameResult("Error: already taken");
            }
            else {
                res.status(500);
                result = new JoinGameResult("Error: server error");
            }
        }
        return new Gson().toJson(result);
    }

    private Object listGames(Request req, Response res) {
        String authToken = req.headers("authorization");

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result;

        try {
            ListGamesService service = new ListGamesService(gameDAO,authDAO);
            result = service.listGames(request);
        }
        catch(RequestException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                result = new ListGamesResult(null, "Error: unauthorized");
            }
            else {
                res.status(500);
                result = new ListGamesResult(null, "Error: server error");
            }
        }

        return new Gson().toJson(result);
    }

    private Object createGame(Request req, Response res) {
        String authToken = req.headers("authorization");

        String json = req.body();
        System.out.println(json);
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        System.out.println(jsonObject);

        String gameName = jsonObject.get("gameName").getAsString();

        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        Object result;

        try {
            CreateGameService service = new CreateGameService(gameDAO,authDAO);
            result = service.createGame(request);
        }
        catch(RequestException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                result = new LogoutResult("Error: unauthorized");
            }
            else if (Objects.equals(e.getMessage(), "bad request")) {
                res.status(400);
                result = new LogoutResult("Error: bad request");
            }
            else {
                res.status(500);
                result = new LogoutResult("Error: server error");
            }
        }

        return new Gson().toJson(result);
    }

    private Object logout(Request req, Response res) {

        LogoutRequest request = new LogoutRequest(req.headers("authorization"));

        LogoutResult result;

        try {
            LogoutService service = new LogoutService(authDAO);
            result = service.logout(request);
        }
        catch(Exception e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                result = new LogoutResult("Error: unauthorized");
            }
            else {
                res.status(500);
                result = new LogoutResult("Error: server error");
            }
        }

        return new Gson().toJson(result);
    }

    private Object login(Request req, Response res) {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);

        LoginResult result;
        try {
            LoginService service = new LoginService(userDAO, authDAO);
            result = service.login(request);
        }
        catch (RequestException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                result = new LoginResult(null, null, "Error: unauthorized");
            }
            else if (Objects.equals(e.getMessage(), "not a valid username")) {
                res.status(401);
                result = new LoginResult(null, null, "Error: not a valid username");
            }
            else {
                res.status(500);
                result = new LoginResult(null, null, "Error: server error");
            }

        }

        return new Gson().toJson(result);
    }

    private Object clear(Request req, Response res) {
        ClearResult result;

        try {
            ClearService service = new ClearService(userDAO, authDAO, gameDAO);
            service.clear();
            result = new ClearResult(null);
        }
        catch(Exception e) {
            res.status(500);
            result = new ClearResult("Error: server error");
        }

        return new Gson().toJson(result);
    }

    private Object registerUser(Request req, Response res) {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);

        RegisterResult result;
        try {
            RegisterService service = new RegisterService(userDAO, authDAO);
            result = service.register(request);
        }
        catch (RequestException e) {
            if (Objects.equals(e.getMessage(), "bad request")) {
                res.status(400);
                result = new RegisterResult(null, null, "Error: bad request");
            }
            else if (Objects.equals(e.getMessage(), "already taken")) {
                res.status(403);
                result = new RegisterResult(null, null, "Error: already taken");
            }
            else {
                res.status(500);
                result = new RegisterResult(null, null, "Error: server error");
            }

        }

        return new Gson().toJson(result);
    }
}
