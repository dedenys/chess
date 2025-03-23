package client;

import java.util.Scanner;

public class PreloginRepl {

    private final ChessClient client;

    public PreloginRepl(String serverUrl) {
        client = new ChessClient();
    }

    public void run() {
        System.out.println("♕ Welcome to chess ♕");
        System.out.print(client.help());
    }
}
