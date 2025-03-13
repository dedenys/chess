package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseAuthDAO implements AuthDAO{

    public DatabaseAuthDAO() {
        try {
            configureDatabase();
        }
        catch (Exception e) {
        }
    }

    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT token, json FROM auth WHERE token=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
    public AuthData createAuth(AuthData auth) {
        try {
            var statement = "INSERT INTO auth (token, username, json) VALUES (?, ?, ?)";
            var json = new Gson().toJson(auth);
            var id = DataBaseHelperFunctions.executeUpdate(statement, auth.authToken(), auth.username(), json);
            return new AuthData(auth.authToken(), auth.username());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public void removeAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auth WHERE token=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void clear() {
        var statement = "TRUNCATE auth";
        try {
            DataBaseHelperFunctions.executeUpdate(statement);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final String[] createStatementsAuth = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `token` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`token`),
              INDEX(token)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsAuth) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        //var username = rs.getString("username");
        var json = rs.getString("json");
        var auth = new Gson().fromJson(json, AuthData.class);
        return auth;
    }
}
