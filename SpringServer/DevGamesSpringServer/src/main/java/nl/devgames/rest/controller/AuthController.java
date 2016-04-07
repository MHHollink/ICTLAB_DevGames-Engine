package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController extends BaseController{

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String,String> login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        if (password == null || password.isEmpty() || username == null || username.isEmpty())
            throw new BadRequestException("Username or password was missing");

        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) WHERE n.username = '%s' AND n.password = '%s' RETURN n.username",
                username,
                password
        );

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject();

        if (hasErrors(jsonResponse)) return null;

        int users = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray().size();

        if (users == 0) {
            L.og("login attempt failed, no user with given combo");
            throw new BadRequestException("This username-password combination is not found");
        } else {
            L.og("User %s has successfully logged in, generating session token...", username);

            java.util.Map<String, String> result = new java.util.HashMap<>();
            // TODO, user some real session management stuff


            String sessionID = String.valueOf(UUID.randomUUID());
            result.put(Application.SESSION_HEADER_KEY, sessionID);

            Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:User {username : '%s'}) SET n.session = '%s'",
                    username,
                    sessionID
            );

            return result;
        }
    }
}
