package client;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


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
