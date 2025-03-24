package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import model.request.LoginRequest;
import model.result.LoginResult;

public class LoginService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest loginRequest) throws RequestException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        UserData user = userDAO.getUser(username);
        LoginResult result;

        if (user != null) {
            if (BCrypt.checkpw(password, user.password())) { // check for valid password
                String token = TokenGenerator.generateToken();
                AuthData auth = new AuthData(token, username);
                authDAO.createAuth(auth);

                result = new LoginResult(username, token, null);
            }
            else {
                throw new RequestException("unauthorized");
            }
        }
        else {
            throw new RequestException("not a valid username");
        }
        return result;
    }
}
