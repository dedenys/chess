package client;

import model.result.LoginResult;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private final PreloginClient preLoginClient;
    private final LoggedinClient loggedinClient;
    private final GameClient gameClient;
    private State state = State.PRELOGIN;

    public Repl(String serverUrl) {
        preLoginClient = new PreloginClient(serverUrl);
        loggedinClient = new LoggedinClient(serverUrl);
        gameClient = new GameClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to chess ♕");


        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (preLoginClient.state == State.LOGGEDIN) {
                state = State.LOGGEDIN;
            }

            printPrompt();
            String line = scanner.nextLine();

            try {
                result = switch (state) {
                    case PRELOGIN -> preLoginClient.eval(line);
                    case LOGGEDIN -> loggedinClient.eval(line);
                    case GAME -> gameClient.eval(line);
                };
                //result = preLoginClient.eval(line);
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
