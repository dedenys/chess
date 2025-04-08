package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void loadGame(String username, ChessGame game) throws IOException {
        System.out.println("in load game");
        LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        String result = new Gson().toJson(message, LoadGameMessage.class);
        var c = connections.get(username);
        c.send(result);
    }

    public void broadcast(String excludeVisitorName, NotificationMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                String result = new Gson().toJson(notification, NotificationMessage.class);
                //c.send(result);
                if (!c.name.equals(excludeVisitorName)) {
                    //System.out.println(notification.getMessage());
                    //c.send(notification.toString());
                    c.send(result);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.name);
        }
    }
}
