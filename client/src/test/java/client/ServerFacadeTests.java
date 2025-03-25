package client;

import model.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerValidTest() {

        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);
        RegisterRequest newRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult result = null;


        try {
            serverFacade.clear();
            result = serverFacade.register(newRequest);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        Assertions.assertNotNull(result);
    }

    @Test
    public void registerInvalidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);
        RegisterRequest newRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult result = null;
        try {
            result = serverFacade.register(newRequest);
        }
        catch(Exception e) {

        }
        Assertions.assertNull(result);
    }

    @Test
    public void loginValidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        LoginRequest newRequest = new LoginRequest("newuser", "pass");
        LoginResult result = null;
        try {
            serverFacade.clear();
            serverFacade.register(newRegisterRequest);
            result = serverFacade.login(newRequest);
        }
        catch(Exception e) {

        }
        Assertions.assertNotNull(result);
    }

    @Test
    public void loginInvalidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);
        LoginRequest newRequest = new LoginRequest("newuser", "wrongpass");
        LoginResult result = null;
        try {
            result = serverFacade.login(newRequest);
        }
        catch(Exception e) {

        }
        Assertions.assertNull(result);
    }

    @Test
    public void createValidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        CreateGameResult result = null;

        try {
            serverFacade.clear();
            resultRegister = serverFacade.register(newRegisterRequest);
            String auth = resultRegister.authToken();

            CreateGameRequest createRequest = new CreateGameRequest(auth,"newusersgame");
            result = serverFacade.createGame(createRequest, auth);

        }
        catch(Exception e) {

        }
        Assertions.assertNotNull(result);
    }

    @Test
    public void createInvalidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        CreateGameResult result = null;

        try {
            serverFacade.clear();
            resultRegister = serverFacade.register(newRegisterRequest);
            String auth = resultRegister.authToken();

            // invalid auth
            CreateGameRequest createRequest = new CreateGameRequest("wrongauth","newusersgame");
            result = serverFacade.createGame(createRequest, "wrongauth");

        }
        catch(Exception e) {

        }
        Assertions.assertNull(result);
    }

    @Test
    public void joinValidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        JoinGameResult result = null;

        try {
            serverFacade.clear();
            resultRegister = serverFacade.register(newRegisterRequest);
            String auth = resultRegister.authToken();

            CreateGameRequest createRequest = new CreateGameRequest(auth,"newusersgame");
            serverFacade.createGame(createRequest, auth);

            JoinGameRequest joinRequest = new JoinGameRequest(auth, "BLACK", 1);
            result = serverFacade.joinGame(joinRequest, auth);

        }
        catch(Exception e) {

        }
        Assertions.assertNotNull(result);
    }

    @Test
    public void joinInvalidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        JoinGameResult result = null;

        try {
            serverFacade.clear();
            resultRegister = serverFacade.register(newRegisterRequest);
            String auth = resultRegister.authToken();

            CreateGameRequest createRequest = new CreateGameRequest(auth,"newusersgame");
            serverFacade.createGame(createRequest, auth);

            // try to join a nonexistant game
            JoinGameRequest joinRequest = new JoinGameRequest(auth, "BLACK", 2);
            result = serverFacade.joinGame(joinRequest, auth);

        }
        catch(Exception e) {

        }
        Assertions.assertNull(result);
    }

    @Test
    public void listValidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        Collection<GameData> result = null;

        try {
            serverFacade.clear();
            resultRegister = serverFacade.register(newRegisterRequest);
            String auth = resultRegister.authToken();

            CreateGameRequest createRequest = new CreateGameRequest(auth,"newusersgame");
            serverFacade.createGame(createRequest, auth);

            result = serverFacade.listGames(auth);

        }
        catch(Exception e) {

        }
        Assertions.assertNotNull(result);
    }

    @Test
    public void listInvalidTest() {
        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);

        RegisterRequest newRegisterRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult resultRegister = null;

        Collection<GameData> result = null;

        try {
            serverFacade.clear();
            resultRegister = serverFacade.register(newRegisterRequest);
            String auth = resultRegister.authToken();

            CreateGameRequest createRequest = new CreateGameRequest(auth,"newusersgame");
            serverFacade.createGame(createRequest, auth);

            // try to list with bad auth
            result = serverFacade.listGames("wrongauth");

        }
        catch(Exception e) {

        }
        Assertions.assertNull(result);
    }

    @Test
    public void clearTest() {

        String url = "http://localhost:"+String.valueOf(port);
        System.out.println(url);
        ServerFacade serverFacade;
        serverFacade = new ServerFacade(url);
        RegisterRequest newRequest = new RegisterRequest("newuser", "pass", "email");
        RegisterResult result = null;

        LoginRequest loginRequest = new LoginRequest("newuser", "pass");
        LoginResult resultLogin = null;
        try {
            serverFacade.clear();
            result = serverFacade.register(newRequest);
            serverFacade.clear();
            resultLogin = serverFacade.login(loginRequest);
        }
        catch(Exception e) {
            System.out.println(e);
        }
        Assertions.assertNotNull(result);
    }


}
