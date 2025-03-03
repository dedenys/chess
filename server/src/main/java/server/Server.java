package server;

import com.google.gson.Gson;
import dataaccess.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.ClearResult;
import result.LoginResult;
import result.RegisterResult;
import service.*;
import service.Service;
import spark.*;

import java.util.Objects;

public class Server {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
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
