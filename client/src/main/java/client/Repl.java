package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import model.GameData;
import model.result.LoginResult;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {

    private final PreloginClient preLoginClient;
    private final LoggedinClient loggedinClient;
    private final GameClient gameClient;
    private State state = State.PRELOGIN;
    private GameData game = null;



    public Repl(String serverUrl) {
        preLoginClient = new PreloginClient(serverUrl);
        loggedinClient = new LoggedinClient(serverUrl);
        gameClient = new GameClient(serverUrl, this);
    }

    public void run() {
        System.out.print(BLUE);
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
                gameClient.setAuth(loggedinClient.auth);
                gameClient.setGameID(loggedinClient.gameID);
                gameClient.connect();
                loggedinClient.state = State.LOGGEDIN;
            }
            if (GameClient.isLeaving) {
                state = State.LOGGEDIN;
                GameClient.isLeaving = false;

            }

            printPrompt();
            String line = scanner.nextLine();

            try {
                //System.out.println(state);
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

    @Override
    public void notify(NotificationMessage notification) {
        //System.out.println(notification);
        System.out.println(RED + notification.getMessage());

        printPrompt();
    }

    @Override
    public void load(LoadGameMessage message) {
        ChessGame game = message.getGame();
        GameClient.testGame = game;
        GameClient.testBoard = game.getBoard();
        GameClient.currentTurn = game.getTeamTurn();
        System.out.println("\n");
        GameClient.draw(null, null);
    }


//    public void notify(ServerMessage message) {
//        //System.out.println("in notify");
//        //System.out.println(message.getServerMessageType());
////        switch (message.getServerMessageType()) {
////            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
////            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
////            case LOAD_GAME -> test(((LoadGameMessage) message));
////        }
//
//        GameClient.draw(null, null);
//    }

    private void displayNotification(String message) {
        System.out.println("displaying notification");
        System.out.println(RED+message);
        printPrompt();
    }

    private void displayError(String errorMessage) {
        System.out.println(RED+errorMessage);
        printPrompt();
    }

    private void test(LoadGameMessage message) {
        System.out.println("trying to load game");
        ChessGame game = message.getGame();
    }

    private void loadGame(ChessGame game) {
        System.out.println("in client load game");
        GameClient.testGame = game;
        GameClient.testBoard = game.getBoard();
        GameClient.draw(null, null);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
