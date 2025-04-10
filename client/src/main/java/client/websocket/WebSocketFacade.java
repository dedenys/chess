package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    //System.out.println(message);
                    LoadGameMessage load = null;
                    ErrorMessage error = null;
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    ServerMessage.ServerMessageType t = notification.getServerMessageType();
                    if (t == ServerMessage.ServerMessageType.LOAD_GAME) {
                        load = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.load(load);
                    }
                    else if (t == ServerMessage.ServerMessageType.ERROR) {
                        error = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.error(error);
                    }
                    else {
                        notificationHandler.notify(notification);
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(String auth, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command, UserGameCommand.class));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void resign(String auth, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN,auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command, UserGameCommand.class));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void leave(String auth, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE,auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command, UserGameCommand.class));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void makeMove(String auth, int gameID, ChessMove m) throws Exception {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameID, m);
            this.session.getBasicRemote().sendText(new Gson().toJson(command, MakeMoveCommand.class));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

//    public void leavePetShop(String visitorName) throws Exception {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new Exception(ex.getMessage());
//        }
//    }

}

