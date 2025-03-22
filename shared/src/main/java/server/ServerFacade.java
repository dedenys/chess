package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public UserData register(UserData user) throws Exception {
        var path = "/user";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }

    public UserData login(UserData user) throws Exception {
        var path = "/session";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }

    public UserData logout(UserData user) throws Exception {
        var path = "/session";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }

    public GameData createGame(GameData game) throws Exception {
        var path = "/game";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }

    public GameData joinGame(int id) throws Exception {
        var path = "/game";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }

    public GameData[] listGames() throws Exception {
        var path = "/game";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }

    public void clour() throws Exception {
        var path = "/db";
        //return this.makeRequest("POST", path, pet, Pet.class);
    }


}
