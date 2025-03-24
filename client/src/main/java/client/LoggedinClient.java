package client;

import server.ServerFacade;

import java.util.Arrays;

public class LoggedinClient {
    private final String serverUrl;
    private final ServerFacade server;

    public LoggedinClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String help() {
        return """
                    - help
                    - logout
                    - create <gamename>
                    - list
                    - observe
                    - quit
                    """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
