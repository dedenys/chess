package client;

import model.UserData;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import server.ServerFacade;

import java.util.Arrays;

public class PreloginClient {

    private String userName = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state = State.PRELOGIN;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String help() {
            return """
                    - help
                    - register <yourname>
                    - login <yourname>
                    - quit
                    """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length >= 3) {
            state = State.LOGGEDIN;
            userName = params[0];
            String pass = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(userName, pass, email);
            server.register(request);
            return String.format("You signed in as %s.", userName);
        }
        throw new Exception("Expected: <yourname>");
    }

    public String login(String... params) throws Exception {
        if (params.length >= 2) {
            state = State.LOGGEDIN;
            userName = params[0];
            String pass = params[1];
            LoginRequest request = new LoginRequest(userName, pass);
            server.login(request);
            return String.format("You signed in as %s.", userName);
        }
        throw new Exception("Expected: <yourname>");
    }
}
