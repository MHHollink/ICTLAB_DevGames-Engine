package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.UnknownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The AuthController manages the /Login calls for the DevGames Server.
 * It generates SessionTokens and Sends updates users with this token
 */
@RestController
public class AuthController extends BaseController{

    /**
     *
     * @param username Plain text string value of the username
     * @param password SHA-256 Hash value for the password
     * @return
     *      Map containing ONE session token.
     *      {@link nl.devgames.Application#SESSION_HEADER_KEY} is used for the key.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String,String> login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {

        L.d("Called");

        if (password == null || password.isEmpty() || username == null || username.isEmpty()) {
            L.w("Throwing BadRequestException, Username or password was missing...");
            throw new BadRequestException("Username or password was missing");
        }

        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("username", username);
            fields.put("password", password);

            UserDao dao = new UserDao();

            List<User> users = dao.queryByFields(fields);

            if (users.size() == 0) {
                L.w("login attempt failed, no user with given combo");
                throw new BadRequestException("This username-password combination is not found");
            } else {
                if(users.size() == 2) L.e("WAIT! what?! multiple users were returned by username '%s'", username);

                Map<String, String> result = new java.util.HashMap<>();
                String sessionID = String.valueOf(UUID.randomUUID());
                // todo : user some real session management stuff

                result.put(Application.SESSION_HEADER_KEY, sessionID);
                User user = users.get(0);
                if(user == null) throw new UnknownInternalServerError("Something strange happened while logging in...");

                user.setSessionId(sessionID);

                dao.update(user);

                return result;
            }

        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
            throw new DatabaseOfflineException();
        }
    }
}
