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
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserController extends BaseController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User getOwnUser(HttpServletRequest request) {
        User caller = getUserFromSession( getSession( request ) );
        L.og("Called");
        return caller;
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public User updateOwnUser(HttpServletRequest request, @RequestBody User userWithUpdateFields) {
        L.og("Called");
        if(userWithUpdateFields == null) {
            L.og("Update user received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }
        User caller = getUserFromSession( getSession( request ) );

        if(userWithUpdateFields.getUsername() != null)
            caller.setGitUsername(userWithUpdateFields.getUsername());

        if(userWithUpdateFields.getGitUsername() != null)
            caller.setGitUsername(userWithUpdateFields.getGitUsername());

        if(userWithUpdateFields.getFirstName() != null)
            caller.setFirstName(userWithUpdateFields.getFirstName());

        if(userWithUpdateFields.getTween() != null)
            caller.setTween(userWithUpdateFields.getTween());

        if(userWithUpdateFields.getLastName() != null)
            caller.setLastName(userWithUpdateFields.getLastName());

        if(userWithUpdateFields.getGcmId() != null )
            caller.setGcmId(userWithUpdateFields.getGcmId());

        if(userWithUpdateFields.getAge() != 0 )
            caller.setAge(userWithUpdateFields.getAge());

        if(userWithUpdateFields.getMainJob() != null)
            caller.setMainJob(userWithUpdateFields.getMainJob());

        // TODO, update it in neo and return 200

        throw new UnsupportedOperationException("This will return a an ok if user is updated");
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createNewUser(@RequestBody User user) {
        L.og("Called");
        throw new UnsupportedOperationException("This shall be used to create users");
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(HttpServletRequest request, @PathVariable Long id) {
        User caller = getUserFromSession( request.getHeader(Application.SESSION_HEADER_KEY) );
        L.og("Called");
        return getUserFromQuery("MATCH (n:User) WHERE ID(n) = %d RETURN {id:id(n), labels: labels(n), data: n}", id);
    }





      /*************************/
     /**  Convince methods   **/
    /*************************/

    /**
     *
     * @param request the http request containing headers and such
     * @return json array with all data
     */
    @Deprecated
    private JsonObject getUserJsonFromRequest(HttpServletRequest request) {
        String session = request.getHeader(Application.SESSION_HEADER_KEY); // gets the session from the headers

        if (session == null || session.isEmpty())
            throw new InvalidSessionException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) WHERE n.session = '%s' RETURN {id:id(n), labels: labels(n), data: n}",
                session
        ); // Request to neo4j

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject(); // parse neo4j response
        JsonArray errors = jsonResponse.get("errors").getAsJsonArray(); // get the list of errors

        if (errors.size() != 0) { // Check if there are more the 0 errors
            for (JsonElement error : errors) L.og(error.getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errors.getAsString()); // throws exception with errors
        }

        JsonArray rows = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        if(rows.size() == 0) throw new InvalidSessionException("Request session is not found");

        return rows.get(0).getAsJsonObject().get("row").getAsJsonArray().get(0).getAsJsonObject(); // Returns user object
    }

    @Deprecated
    private JsonArray getUsersFromQuery(HttpServletRequest request, String query, Object... params) {
        if(getUserJsonFromRequest(request) == null)
            throw new InvalidSessionException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                query,
                params
        ); // Request to neo4j

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject(); // parse neo4j response

        if(hasErrors(jsonResponse)) return null;

        JsonArray data = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();

        if (data.size() == 0)
            throw new NotFoundException("The server could not find the requested data...");

        return data.get(0).getAsJsonObject().get("row").getAsJsonArray();
    }

    private User getUserFromQuery(String query, Object... params) {
        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                query,
                params
        ); // Request to neo4j

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject(); // parse neo4j response

        if (hasErrors(jsonResponse)) return null;

        JsonArray data = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();

        if (data.size() == 0)
            throw new NotFoundException("The server could not find the requested data...");

        JsonArray rows = data.get(0).getAsJsonObject().get("row").getAsJsonArray();

        if (rows.size() == 0)
            throw new NotFoundException("The server could not find the requested data...");

        return new User().createFromJsonObject(rows.get(0).getAsJsonObject());
    }

    private List<User> getUsersFromQuery(String query, Object... params) {
        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                query,
                params
        ); // Request to neo4j

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject(); // parse neo4j response

        if(hasErrors(jsonResponse)) return null;

        JsonArray data = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();

        if (data.size() == 0)
            throw new NotFoundException("The server could not find the requested data...");

        return new User().createFromJsonArray(data.get(0).getAsJsonObject().get("row").getAsJsonArray());
    }

}
