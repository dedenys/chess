package client;

import model.GameData;
import server.ServerFacade;

import java.util.Arrays;

public class GameClient {
    private final String serverUrl;
    private final ServerFacade server;
    public GameData game;

    public GameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String help() {
        return """
                    - help
                    - do something
                    """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "do" -> test();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String test() {
        System.out.println(game);
        return "Hello!";
    }
}
