package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
import java.util.Objects;
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

            System.out.println("message received");
            String username = getUsername(command.getAuthToken());
            System.out.println(username);
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
            System.out.println("caught exception");
            sendMessage(session.getRemote(), new ErrorMessage(ERROR,e.getMessage()));
    }
 //       UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
 //       NotificationMessage n = new NotificationMessage(NOTIFICATION, "test!");
//        connections.broadcast("test_websocket", n);
//        switch (action.type()) {
//            case ENTER -> enter(action.visitorName(), session);
//            case EXIT -> exit(action.visitorName());
//        }
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) throws IOException {
        String json = new Gson().toJson(errorMessage, ErrorMessage.class);
        remote.sendString(json);
    }

    private String getUsername(String authToken) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: bad authentication ;(");
        }
        String name = auth.username();
        return name;
    }

    private void saveSession(String user, Session session) throws Exception {
        connections.add(user, session);
    }

    private void connect(Session session, String username, UserGameCommand command) throws Exception {
        System.out.println("in connect");
        var message = String.format("%s is in the game", username);
        int id = command.getGameID();
        //System.out.println(id);
        GameData data = gameDAO.getGame(id);
        //System.out.println(data);
        if (data == null) {
            throw new Exception("Error: bad gameID ;(");
        }
        //System.out.println("skipped");
        ChessGame game = data.game();
        String color = null;
        if (Objects.equals(data.whiteUsername(), username)) {
            color = "white";
        }
        if (Objects.equals(data.blackUsername(), username)) {
            color = "black";
        }
        if (color != null) {
            message += " as " + color;
        }
        System.out.println("about to notify");
        connections.joinUserToGame(username, command.getGameID());
        connections.broadcast(id, username, new NotificationMessage(NOTIFICATION, message));
        connections.loadGame(username, game);
    }

    private void error() {

    }

    private String positionToText(ChessPosition p) {
        int row = p.getRow();
        int col = p.getColumn();
        String rowNum = "";
        String colChar = "";

        switch (row) {
            case 1 -> rowNum = "1";
            case 2 -> rowNum = "2";
            case 3 -> rowNum = "3";
            case 4 -> rowNum = "4";
            case 5 -> rowNum = "5";
            case 6 -> rowNum = "6";
            case 7 -> rowNum = "7";
            case 8 -> rowNum = "8";
        }
        switch (col) {
            case 1 -> colChar = "h";
            case 2 -> colChar = "g";
            case 3 -> colChar = "f";
            case 4 -> colChar = "e";
            case 5 -> colChar = "d";
            case 6 -> colChar = "c";
            case 7 -> colChar = "b";
            case 8 -> colChar = "a";
        }
        return (colChar+rowNum);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        try {
            int id = command.getGameID();
            ChessMove m = command.getMove();
            System.out.println("1");
            GameData gameData = gameDAO.getGame(id);
            System.out.println(gameData);
            ChessGame game = gameData.game();

            ChessPosition start = m.getStartPosition();
            ChessPiece p = game.getBoard().getPiece(start);
            ChessGame.TeamColor c = p.getTeamColor();

            if (game.isOver) {
                throw new Exception("game is already over");
            }

            if (!Objects.equals(username, gameData.blackUsername()) && !Objects.equals(username, gameData.whiteUsername())) {
                throw new Exception("cannot make move as observer");
            }

            if (Objects.equals(gameData.whiteUsername(), username) && !(c == ChessGame.TeamColor.WHITE)) {
                throw new Exception("wrong piece color");
            }
            if (Objects.equals(gameData.blackUsername(), username) && !(c == ChessGame.TeamColor.BLACK)) {
                throw new Exception("wrong piece color");
            }

            try {
                game.makeMove(m);
            } catch (Exception e) {
                throw new Exception("invalid move");
            }


            ChessGame.TeamColor checkColor;
            String checkUserName;
            if (c == ChessGame.TeamColor.WHITE) {
                checkColor = ChessGame.TeamColor.BLACK;
                checkUserName = gameData.blackUsername();
            }
            else {
                checkColor = ChessGame.TeamColor.WHITE;
                checkUserName = gameData.whiteUsername();
            }

            if (checkUserName == null) {
                checkUserName = "EMPTY";
            }

            boolean check = game.isInCheck(checkColor);
            boolean checkMate = game.isInCheckmate(checkColor);
            boolean staleMate = game.isInStalemate(checkColor);

            if (checkMate || staleMate) {
                game.isOver = true;
            }

            System.out.println("2");
            GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            String json = new Gson().toJson(newGameData, GameData.class);
            gameDAO.updateGame(id, json);
            System.out.println(game);
            connections.broadcastGame(id, game);
            String startPositionText = positionToText(m.getStartPosition());
            String endPositionText = positionToText(m.getEndPosition());
            String moveMessage = String.format("%s moved %s to %s", username, startPositionText, endPositionText);
            connections.broadcast(id, username, new NotificationMessage(NOTIFICATION, moveMessage));



            if (check) {
                String checkText = String.format("%s (%s) is in check", checkUserName, checkColor);
                NotificationMessage checkMessage = new NotificationMessage(NOTIFICATION, checkText);
                connections.broadcast(id, "", checkMessage);
            }
            if (checkMate) {
                String checkText = String.format("%s (%s) is in checkmate.", checkUserName, checkColor);
                NotificationMessage checkMessage = new NotificationMessage(NOTIFICATION, checkText);
                connections.broadcast(id, "", checkMessage);
            }
            if (staleMate) {
                String checkText = String.format("%s (%s) is in stalemate", checkUserName, checkColor);
                NotificationMessage checkMessage = new NotificationMessage(NOTIFICATION, checkText);
                connections.broadcast(id, "", checkMessage);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }

    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws Exception{
        try {
            int id = command.getGameID();
            GameData gameData = gameDAO.getGame(id);
            ChessGame game = gameData.game();

            GameData newGameData = gameData;
            if (Objects.equals(username, gameData.blackUsername())) {
                newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), game);
            }
            if (Objects.equals(username, gameData.whiteUsername())) {
                newGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), game);
            }

            String json = new Gson().toJson(newGameData, GameData.class);
            gameDAO.updateGame(id, json);
            String message = String.format("%s left the game", username);
            connections.leaveUserFromGame(username);
            connections.broadcast(id, username, new NotificationMessage(NOTIFICATION, message));
            connections.removeConnection(username);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }
    }

    private void resign(Session session, String username, UserGameCommand command) throws Exception {
        try {
            int id = command.getGameID();
            GameData gameData = gameDAO.getGame(id);
            ChessGame game = gameData.game();

            if (game.isOver) {
                throw new Exception("game is already over");
            }
            game.isOver = true;


            if (!Objects.equals(username, gameData.blackUsername()) && !Objects.equals(username, gameData.whiteUsername())) {
                throw new Exception("cannot resign as observer");
            }

            GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            String json = new Gson().toJson(newGameData, GameData.class);
            gameDAO.updateGame(id, json);
            String message = String.format("%s resigned", username);
            connections.broadcast(id, "", new NotificationMessage(NOTIFICATION, message));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }
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