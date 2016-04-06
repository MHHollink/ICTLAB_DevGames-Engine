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

@RestController
public class UserController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User getOwnUser(HttpServletRequest request) {
        L.og("Called");
        return new User().createFromJsonObject( getUserJsonFromRequest( request ) );
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public User updateOwnUser(HttpServletRequest request, @RequestBody User userWithUpdateFields) {
        L.og("Called");
        if(userWithUpdateFields == null) {
            L.og("Update user received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }
        User user = new User().createFromJsonObject( getUserJsonFromRequest( request ) );

        if(userWithUpdateFields.getUsername() != null)
            user.setGitUsername(userWithUpdateFields.getUsername());

        if(userWithUpdateFields.getGitUsername() != null)
            user.setGitUsername(userWithUpdateFields.getGitUsername());

        if(userWithUpdateFields.getFirstName() != null)
            user.setFirstName(userWithUpdateFields.getFirstName());

        if(userWithUpdateFields.getTween() != null)
            user.setTween(userWithUpdateFields.getTween());

        if(userWithUpdateFields.getLastName() != null)
            user.setLastName(userWithUpdateFields.getLastName());

        if(userWithUpdateFields.getGcmId() != null )
            user.setGcmId(userWithUpdateFields.getGcmId());

        if(userWithUpdateFields.getAge() != 0 )
            user.setAge(userWithUpdateFields.getAge());

        if(userWithUpdateFields.getMainJob() != null)
            user.setMainJob(userWithUpdateFields.getMainJob());

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
        L.og("Called");
        JsonArray array = getUsersFromQuery(request, "MATCH (n:User) WHERE ID(n) = %d RETURN {id:id(n), labels: labels(n), data: n}", id);
        return array.size() != 0 ? new User().createFromJsonObject(array.get(0).getAsJsonObject()) : null ;
    }





      /*************************/
     /**  Convince methods   **/
    /*************************/

    /**
     *
     * @param request the http request containing headers and such
     * @return json array with all data
     */
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

    protected JsonArray getUsersFromQuery(HttpServletRequest request, String query, Object... params) {
        if(getUserJsonFromRequest(request) == null)
            throw new InvalidSessionException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                query,
                params
        ); // Request to neo4j

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject(); // parse neo4j response
        JsonArray errors = jsonResponse.get("errors").getAsJsonArray(); // get the list of errors

        if (errors.size() != 0) { // Check if there are more the 0 errors
            for (JsonElement error : errors) L.og(error.getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errors.getAsString()); // throws exception with errors
        }

        JsonArray data = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();

        if (data.size() == 0)
            throw new NotFoundException("The server could not find the requested data...");

        return data.get(0).getAsJsonObject().get("row").getAsJsonArray();
    }
}
