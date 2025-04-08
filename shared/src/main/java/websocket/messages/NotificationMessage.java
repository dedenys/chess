package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {

    public final String message;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "message='" + message + '\'' +
                '}';
    }

    public NotificationMessage(ServerMessageType type, String m) {
        super(type);
        message = m;
    }

    public String getMessage() {
        return message;
    }
}
