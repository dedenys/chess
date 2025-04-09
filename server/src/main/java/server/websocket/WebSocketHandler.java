package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
//import exception.ResponseException;
import dataaccess.AuthDAO;
import dataaccess.DatabaseAuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.request.JoinGameNoAuth;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
//import webSocketMessages.Action;
//import webSocketMessages.Notification;

import java.io.IOException;
import java.util.Timer;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            //System.out.println(message);
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            MakeMoveCommand makeMoveCommand = null;

            UserGameCommand.CommandType t = command.getCommandType();
            if (t == UserGameCommand.CommandType.MAKE_MOVE) {
                makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            }

            String username = getUsername(command.getAuthToken());

            //saveSession(command.getGameID(), session);
            saveSession(username, session);

            switch (t) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, makeMoveCommand);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }

        }
    catch (Exception e) {
            sendMessage(session.getRemote(), new ErrorMessage(ERROR,"websocket error :("));
    }
 //       UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
 //       NotificationMessage n = new NotificationMessage(NOTIFICATION, "test!");
//        connections.broadcast("test_websocket", n);
//        switch (action.type()) {
//            case ENTER -> enter(action.visitorName(), session);
//            case EXIT -> exit(action.visitorName());
//        }
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) {
    }

    private String getUsername(String authToken) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return "NULL USER";
        }
        String name = auth.username();
        return name;
    }

    private void saveSession(String user, Session session) throws Exception {
        connections.add(user, session);
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException {
        System.out.println("in connect");
        var message = String.format("%s is in the game", username);
        connections.broadcast(username, new NotificationMessage(NOTIFICATION, message));
        int id = command.getGameID();
        GameData data = gameDAO.getGame(id);
        ChessGame game = data.game();
        connections.loadGame(username, game);
    }


    private void makeMove(Session session, String username, MakeMoveCommand command) {
        try {
            int id = command.getGameID();
            ChessMove m = command.getMove();
            System.out.println("1");
            GameData gameData = gameDAO.getGame(id);
            System.out.println(gameData);
            ChessGame game = gameData.game();

            game.makeMove(m);

            System.out.println("2");
            GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            String json = new Gson().toJson(newGameData, GameData.class);
            gameDAO.updateGame(id, json);
            System.out.println(game);
            connections.broadcastGame(game);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void leaveGame(Session session, String username, UserGameCommand command) {

    }

    private void resign(Session session, String username, UserGameCommand command) {

    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}