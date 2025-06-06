package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseGameDAO implements GameDAO {

    public DatabaseGameDAO() {
        try {
            configureDatabase();
        }
        catch (Exception e) {
        }
    }

    public GameData getGame(int id) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    public GameData createGame(GameData game) {
        try {
            var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(game);
            var id = DataBaseHelperFunctions.executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
            return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void updateGame(int id, String jsonData) {
        try (var conn = DatabaseManager.getConnection()) {
            GameData game = getGame(id);

            try (var preparedStatement = conn.prepareStatement("UPDATE game SET json=? WHERE id=?")) {
                preparedStatement.setString(1, jsonData);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Collection<GameData> getAllGames() {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void addWhiteUserToGame(int id, String username) {
        try (var conn = DatabaseManager.getConnection()) {
            GameData game = getGame(id);
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUsername=? WHERE id=?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
                game = new GameData(id, username, game.blackUsername(), game.gameName(), game.game());
            }
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET json=? WHERE id=?")) {
                var json = new Gson().toJson(game);
                preparedStatement.setString(1, json);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBlackUserToGame(int id, String username) {
        try (var conn = DatabaseManager.getConnection()) {
            GameData game = getGame(id);
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET blackUsername=? WHERE id=?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
                game = new GameData(id, game.whiteUsername(), username, game.gameName(), game.game());
            }
            try (var preparedStatement = conn.prepareStatement("UPDATE game SET json=? WHERE id=?")) {
                var json = new Gson().toJson(game);
                preparedStatement.setString(1, json);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clear() {
        var statement = "TRUNCATE game";
        try {
            DataBaseHelperFunctions.executeUpdate(statement);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final String[] createStatementsGame = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsGame) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, GameData.class);
        return game;
    }
}
