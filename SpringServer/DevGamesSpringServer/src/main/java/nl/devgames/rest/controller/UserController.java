package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;

@RestController
public class UserController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User getOwnUser(HttpServletRequest request) {
        L.og("* called getOwnUser from %s",request.getHeader(Application.SESSION_HEADER_KEY));

        JsonArray rows = getOwnUserFromSessionToken( request );

        if(rows.size() == 0) {
            throw new InvalidSessionException("Request session is not found");
        }

        Long userId = rows.get(0).getAsLong();
        JsonObject userRow = rows.get(1).getAsJsonObject();

        User user = new User();

        user.setId(userId);
        user.setUsername(userRow.get("username").getAsString());
        user.setGitUsername(userRow.get("gitUsername").getAsString());

        user.setCommits(new HashSet<>());
        user.setProjects(new HashSet<>());

        return user;
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public User updateOwnUser(HttpServletRequest request, @RequestBody User user) {
        L.og("* called updateOwnUser from %s",request.getHeader(Application.SESSION_HEADER_KEY));

        if(user == null) {
            throw new BadRequestException("No body was passed with the request");
        }

        getOwnUserFromSessionToken(request);

        throw new UnsupportedOperationException("This will return a an ok if user is updated");
    }


    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(HttpServletRequest request, @PathVariable Long id) {
        L.og("* called getUser from %s",request.getHeader(Application.SESSION_HEADER_KEY));

        throw new UnsupportedOperationException("This will return a user with id : " + id);
    }


    private JsonArray getOwnUserFromSessionToken(HttpServletRequest request) {
        String session = request.getHeader(Application.SESSION_HEADER_KEY);

        if(session == null || session.isEmpty()) {
            throw new InvalidSessionException("Request without session");
        }

        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                "match (n:User) where n.session = '%s' return ID(n), n",
                session
        );

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject();
        JsonArray errors = jsonResponse.get("errors").getAsJsonArray();

        if(errors.size() != 0) {
            for (JsonElement error : errors) {
                L.og(error.getAsString());
            }
            throw new KnownInternalServerError("InternalServerError: "+ errors.getAsString());
        }

        JsonArray rows = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("row").getAsJsonArray();

        if(rows.size() == 0) {
            throw new InvalidSessionException("Request session is not found");
        }

        return rows;
    }
}
