package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class RegisterService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws RequestException{
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
                throw new RequestException("already taken");
            }

        }
        else {
            throw new RequestException("bad request");
        }

        return result;
    }
}
