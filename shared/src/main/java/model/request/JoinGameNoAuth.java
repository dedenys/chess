package model.request;

public record JoinGameNoAuth(String authToken, String playerColor, int gameID) {
}
