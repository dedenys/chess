package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username) {
        UserData user = users.get(username);
        return user;
    }
    public UserData createUser(UserData user) {
        users.put(user.username(), user);
        return user;
    }
    public void clear() {
        users.clear();
    }
}
