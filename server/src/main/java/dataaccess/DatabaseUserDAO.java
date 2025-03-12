package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public DatabaseUserDAO() {
        try {
            configureDatabase();
        }
        catch (Exception e) {
        }
    }

    public UserData getUser(String username) {
        UserData user = users.get(username);
        return user;
    }
    public UserData createUser(UserData user) {
        try {
            var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(user);
            var id = executeUpdate(statement, user.username(), user.password(), user.email(), json);
            System.out.println("a");
            return new UserData(user.username(), user.password(), user.email());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public void clear() {
        users.clear();
    }
    public int getLength() {
        return users.size();
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
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
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

}
