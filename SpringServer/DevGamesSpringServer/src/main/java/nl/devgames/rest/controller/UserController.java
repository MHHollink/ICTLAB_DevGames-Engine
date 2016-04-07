package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.User;
import nl.devgames.model.UserWithPassword;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UserController extends BaseController {

    @RequestMapping(method = RequestMethod.POST)
    public User createNewUser(@RequestBody UserWithPassword user) {
        L.og("Called");
        throw new UnsupportedOperationException("This shall be used to create users");
    }

    @RequestMapping(method = RequestMethod.GET)
    public User getOwnUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        User caller = getUserFromSession( session );
        L.og("Called");
        return caller;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public User updateOwnUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                              @PathVariable Long id,
                              @RequestBody User userWithUpdateFields)
    {
        L.og("Called");
        if(userWithUpdateFields == null) {
            L.og("Update user received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }
        User caller = getUserFromSession( session );

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

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                          @PathVariable Long id)
    {

        return new HashMap<>();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public User getUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                        @PathVariable Long id)
    {
        User caller = getUserFromSession( session );
        L.og("Called");
        return getUserFromQuery("MATCH (n:User) WHERE ID(n) = %d RETURN {id:id(n), labels: labels(n), data: n}", id);
    }




      /*****************************/
     /**  Convenience methods   **/
    /****************************/

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
