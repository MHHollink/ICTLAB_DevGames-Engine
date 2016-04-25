package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.ModelDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.model.UserWithPassword;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // todo : update the node in the database and return status code 200

        throw new UnsupportedOperationException("This will return the updated user");
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteUser(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                          @PathVariable Long id)
    {
        throw new UnsupportedOperationException("This will return an empty map if the user is deleted");
    }

    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Set<Project> getProjects(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                     @PathVariable Long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get a list of projects from the user id
        throw new UnsupportedOperationException("This will return an list containing all projects the user is involved in");
    }

    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Set<Push> getPushes(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @PathVariable Long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get a list of pushes from the user id
        throw new UnsupportedOperationException("This will return an list containing all pushes under the user");
    }

    @RequestMapping(value = "{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getCommits(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                   @PathVariable Long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get a list of commits from the user id
        throw new UnsupportedOperationException("This will return an list containing all commits under the user");
    }

    @RequestMapping(value = "{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getIssues(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable Long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get a list of issues linked to the user id
        throw new UnsupportedOperationException("This will return an list containing all issues the user created");
    }

    @RequestMapping(value = "{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplications(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @PathVariable Long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get a list of duplications with files linked to the user id
        throw new UnsupportedOperationException("This will return an list containing all duplications the user created");
    }

    @RequestMapping(value = "{id}/businesses", method = RequestMethod.GET)
    public Set<Business> getBusinesses(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                       @PathVariable Long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get a list of Businesses with files linked to the user id
        throw new UnsupportedOperationException("This will return an list containing all duplications the user created");
    }

    /**
     * This private method is used to extract a user object from a database query
     *
     * @param query     String value of a formatted query
     * @param params    Optional parameters used in the {@param query}
     * @return a user found by the {@param query}
     */
    private User getUserFromQuery(String query, Object... params) {
        String jsonResponseString = null; // Request to neo4j
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    query,
                    params
            );
        } catch (ConnectException e) {
            e.printStackTrace();
        }

        User user;
        try{
            user = new UserDTO().createFromNeo4jData(
                    UserDTO.findFirst(jsonResponseString)
            ).toModel();
        } catch (IndexOutOfBoundsException e) {
            L.w(e, "Requested user was not found in database");
            throw new NotFoundException("User not found");
        }

        return user;
    }

    /**
     * This private method is used to extract multiple users from a database query
     *
     * @param query     String value of a formatted query
     * @param params    Optional parameters used in the {@param query}
     * @return a list of all users found by the {@param query}
     */
    private List<User> getUsersFromQuery(String query, Object... params) {
        String jsonResponseString = null; // Request to neo4j
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    query,
                    params
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        JsonArray data = ModelDTO.getNeo4JData(jsonResponseString);

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
