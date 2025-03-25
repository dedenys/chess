package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import model.request.*;
import model.result.*;

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
            return this.makeRequest("register", "POST", path, request, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var path = "/session";
        return this.makeRequest("login","POST", path, request, LoginResult.class, null);
    }

    public LogoutResult logout(LogoutRequest request) throws Exception {
        var path = "/session";
        return this.makeRequest("logout", "DELETE", path, request, LogoutResult.class, null);
    }

    public CreateGameResult createGame(CreateGameRequest request, String auth) throws Exception {
        var path = "/game";
        return this.makeRequest("create", "POST", path, request, CreateGameResult.class, auth);
    }

    public JoinGameResult joinGame(JoinGameRequest game, String auth) throws Exception {
        var path = "/game";
        //JoinGameResult result =
        return this.makeRequest("join", "PUT", path, game, JoinGameResult.class, auth);
    }

    public Collection<GameData> listGames(String auth) throws Exception {
        var path = "/game";
        ListGamesResult result = this.makeRequest("list", "GET", path, null, ListGamesResult.class, auth);
        return result.games();
    }

    public void clear() throws Exception {
        var path = "/db";
        this.makeRequest("clear", "DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String name, String method, String path, Object request, Class<T> responseClass, String auth) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (auth != null) {
                http.addRequestProperty("authorization", auth);
            }

            http.setDoOutput(true);

            //var s = http.getResponseCode();

            writeBody(request, http);
            //s = http.getResponseCode();
            http.connect();
            //s = http.getResponseCode();
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
                //http.addRequestProperty("authorization", ((ListGamesRequest) request).authToken());
                http.addRequestProperty("Content-Type", "application/json");
            }
            //else if (request instanceof CreateGameRequest) {

            //}
            else {
                http.addRequestProperty("Content-Type", "application/json");
            }
            //System.out.println(request);
            String reqData = new Gson().toJson(request);
            //System.out.println(reqData);
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
                    if (String.valueOf(status).equals("403")) {
                        throw new Exception("Already taken.");
                    }
                    if (String.valueOf(status).equals("401")) {
                        throw new Exception("Invalid username or password.");
                    }
                    if (String.valueOf(status).equals("400")) {
                        throw new Exception("Invalid input.");
                    }
                    if (String.valueOf(status).equals("500")) {
                        throw new Exception("Invalid input.");
                    }
                    throw new Exception(String.valueOf(status));
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
