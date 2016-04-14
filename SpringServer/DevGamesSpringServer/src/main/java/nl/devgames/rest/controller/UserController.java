package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.model.UserWithPassword;
import nl.devgames.model.dto.UserDTO;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UserController extends BaseController {

    @RequestMapping(method = RequestMethod.POST)
    public User createNewUser(@RequestBody UserWithPassword user) {
        L.i("Called");
        throw new UnsupportedOperationException("This shall be used to create users");
    }

    @RequestMapping(method = RequestMethod.GET)
    public User getOwnUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        User caller = getUserFromSession( session );
        L.i("Called");
        return caller;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public User getUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                        @PathVariable Long id)
    {
        User caller = getUserFromSession( session );
        L.i("Called");
        return getUserFromQuery("MATCH (n:User) WHERE ID(n) = %d RETURN {id:id(n), labels: labels(n), data: n}", id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public User updateOwnUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                              @PathVariable long id,
                              @RequestBody User userWithUpdateFields)
    {
        L.i("Called");
        if(userWithUpdateFields == null) {
            L.w("Update user received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }
        User caller = getUserFromSession( session );

        if(caller.getId() != id) {
            throw new BadRequestException(
                    String.format(
                            "Session does not match session for user with id '%d'",
                            id
                    )
            );
        }

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

        throw new UnsupportedOperationException("This will return the updated user");
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                          @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an empty map if the user is deleted");
    }

    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public List<Project> getProjects(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                     @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an list containing all projects the user is involved in");
    }

    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public List<Push> getPushes(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an list containing all pushes under the user");
    }

    @RequestMapping(value = "{id}/commits", method = RequestMethod.GET)
    public List<Commit> getCommits(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                   @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an list containing all commits under the user");
    }

    @RequestMapping(value = "{id}/issues", method = RequestMethod.GET)
    public List<Issue> getIssues(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an list containing all issues the user created");
    }

    @RequestMapping(value = "{id}/duplications", method = RequestMethod.GET)
    public List<Duplication> getDuplications(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an list containing all duplications the user created");
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

        return new UserDTO().createFromJsonObject(rows.get(0).getAsJsonObject()).toModel();
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

        List<UserDTO> dtos = new UserDTO().createFromJsonArray(data.get(0).getAsJsonObject().get("row").getAsJsonArray());
        List<User> users = new ArrayList<>();
        for(UserDTO dto : dtos) {
            users.add(dto.toModel());
        }
        return users;
    }

}
