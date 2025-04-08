package server.websocket;

import com.google.gson.Gson;
//import dataaccess.DataAccess;
//import exception.ResponseException;
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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
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
        return "not-implemented";
    }

    private void saveSession(int gameID, Session session) throws Exception {
        //connections.add(gameID, session);
    }

    private void connect(Session session, String username, UserGameCommand command) {

    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {

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