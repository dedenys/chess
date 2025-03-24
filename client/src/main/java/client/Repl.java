package client;

import model.result.LoginResult;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private final PreloginClient preLoginClient;
    private final LoggedinClient

    public Repl(String serverUrl) {
        preLoginClient = new PreloginClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to chess ♕");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
