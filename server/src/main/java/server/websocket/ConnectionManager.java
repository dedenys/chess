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
    public final ConcurrentHashMap<String, Integer> playersInGames = new ConcurrentHashMap<String, Integer>();

    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void joinUserToGame(String username, int gameID) {
        playersInGames.put(username, gameID);
    }

    public void leaveUserFromGame(String username) {
        playersInGames.remove(username);
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

    public void broadcastGame(int gameID, ChessGame game) throws IOException {
        var removeList = new ArrayList<Connection>();
        LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        String result = new Gson().toJson(message, LoadGameMessage.class);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (playersInGames.get(c.name) == gameID) {
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

    public void removeConnection(String name) {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.name.equals(name)) {
                    connections.remove(c.name);
                }
            }
        }
    }

    public void broadcast(int gameID, String excludeVisitorName, NotificationMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                String result = new Gson().toJson(notification, NotificationMessage.class);
                //c.send(result);
                if (!c.name.equals(excludeVisitorName)) {
                    //System.out.println(notification.getMessage());
                    //c.send(notification.toString());
                    if (playersInGames.get(c.name) == gameID) {
                        c.send(result);
                    }
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
