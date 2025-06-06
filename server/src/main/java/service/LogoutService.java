package service;

import dataaccess.AuthDAO;
import model.request.LogoutRequest;
import model.result.LogoutResult;

public class LogoutService {

    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(LogoutRequest logoutRequest)  throws RequestException {
        String token = logoutRequest.authToken();

        if (authDAO.getAuth(token) != null) {
            authDAO.removeAuth(token);
            return new LogoutResult(null);
        }
        else {
            throw new RequestException("unauthorized");
        }
    }
}
