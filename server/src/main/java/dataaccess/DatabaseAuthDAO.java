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
            System.out.println("A");
            var statement = "SELECT token, json FROM auth WHERE token=?";
            try (var ps = conn.prepareStatement(statement)) {
                System.out.println("B");
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    System.out.println("C");
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
            var id = executeUpdate(statement, auth.authToken(), auth.username(), json);
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
            executeUpdate(statement);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private int executeUpdate(String statement, Object... params) throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (Exception e) {
            throw new Exception(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
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
            for (var statement : createStatements) {
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
