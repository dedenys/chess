package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    UserData createUser(UserData user);
}
