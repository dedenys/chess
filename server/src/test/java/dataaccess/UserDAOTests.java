package dataaccess;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    DatabaseUserDAO d = new DatabaseUserDAO();

    @BeforeEach
    public void resetDatabase() {
        d.clear();
    }

    @Test
    public void createUserSuccessful() {
        UserData user = new UserData("bob", "123", "bob@gmail");

        assertEquals(d.createUser(user), user);
    }
    @Test
    public void createUserFailure() {
        UserData user = new UserData(null, "123", "bob@gmail");

        assertNull(d.createUser(user));
    }
    @Test
    public void getUserSuccessful() {
        UserData user = new UserData("bob", "123", "bob@gmail");

        d.createUser(user);

        assertEquals(d.getUser("bob"), user);
    }
    @Test
    public void getUserFailure() {
        UserData user = new UserData("bob", "123", "bob@gmail");

        assertNull(d.getUser("john"));
    }
    @Test
    public void clear() {
        UserData user = new UserData("bob", "123", "bob@gmail");
        UserData user2 = new UserData("john", "pass", "john@gmail");

        d.createUser(user);
        d.createUser(user2);

        assertEquals(d.getUser("bob"), user);
        assertEquals(d.getUser("john"), user2);

        d.clear();

        assertNull(d.getUser("bob"));
        assertNull(d.getUser("john"));
    }

}
