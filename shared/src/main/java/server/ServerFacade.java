package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import model.request.ListGamesRequest;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ListGamesResult;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public RegisterResult register(RegisterRequest request) throws Exception {
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request) throws Exception {
        var path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }

    public GameData createGame(GameData game) throws Exception {
        var path = "/game";
        return this.makeRequest("POST", path, game, GameData.class);
    }

    public GameData joinGame(GameData game) throws Exception {
        var path = "/game";
        return this.makeRequest("PUT", path, game, GameData.class);
    }

    public Collection<GameData> listGames(ListGamesRequest request) throws Exception {
        var path = "/game";
        ListGamesResult result = this.makeRequest("GET", path, request, ListGamesResult.class);
        return result.games();
    }

    public void clear() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw ex;
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            if (request instanceof LogoutRequest) {
                http.addRequestProperty("authorization", ((LogoutRequest) request).authToken());
            }
            else if (request instanceof ListGamesRequest) {
                http.addRequestProperty("authorization", ((ListGamesRequest) request).authToken());
                http.addRequestProperty("Content-Type", "application/json");
            }
            else {
                http.addRequestProperty("Content-Type", "application/json");
            }
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw new Exception("not successful :(");
                }
            }

            throw new Exception("other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
