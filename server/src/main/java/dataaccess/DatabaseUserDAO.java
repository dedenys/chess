package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO() {
        try {
            configureDatabase();
        }
        catch (Exception e) {
        }
    }

    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
    public UserData createUser(UserData user) {
        try {
            var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(user);
            var id = executeUpdate(statement, user.username(), user.password(), user.email(), json);
            return new UserData(user.username(), user.password(), user.email());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public void clear() {
        var statement = "TRUNCATE user";
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
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
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

    private final String[] createStatementsUser = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsUser) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        var user = new Gson().fromJson(json, UserData.class);
        return user;
    }

}
