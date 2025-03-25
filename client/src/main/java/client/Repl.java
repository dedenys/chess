package client;

import chess.ChessGame;
import model.GameData;
import model.result.LoginResult;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private final PreloginClient preLoginClient;
    private final LoggedinClient loggedinClient;
    private final GameClient gameClient;
    private State state = State.PRELOGIN;
    private GameData game = null;



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
                loggedinClient.state = State.LOGGEDIN;
                loggedinClient.auth = preLoginClient.auth;
                preLoginClient.state = State.PRELOGIN;
            }
            if (loggedinClient.state == State.PRELOGIN) {
                state = State.PRELOGIN;
                preLoginClient.state = State.PRELOGIN;
                loggedinClient.state = State.LOGGEDIN;
            }
            if (loggedinClient.state == State.GAME) {
                state = State.GAME;
                game = loggedinClient.game;
                gameClient.gameData = game;
                gameClient.game = game.game();
                gameClient.color = loggedinClient.color;
                loggedinClient.state = State.LOGGEDIN;
            }
            if (GameClient.isLeaving) {
                state = State.LOGGEDIN;
                GameClient.isLeaving = false;

            }

            printPrompt();
            String line = scanner.nextLine();

            try {
                System.out.println(state);
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
