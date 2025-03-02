package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {

        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        RegisterResult result;

        if (username != null && password != null && email != null) { // check for bad request
            if(userDAO.getUser(username) == null) { // make sure user is not already there
                UserData newUser = new UserData(username, password, email);
                userDAO.createUser(newUser);

                String token = TokenGenerator.generateToken();
                AuthData auth = new AuthData(token, username);
                authDAO.createAuth(auth);
                result = new RegisterResult(username, token, null);
            }
            else {
                result = new RegisterResult(null, null, "Error: already taken");
            }

        }
        else {
            result = new RegisterResult(null, null, "Error: bad request");
        }

        return result;
    }
}
