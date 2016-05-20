package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.UserAlreadyExistsException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
public class UserController extends BaseController {

    @RequestMapping(method = RequestMethod.POST)
    public User createNewUser(@RequestBody User user) {
        L.i("Called");
        L.t("Creating user: %s", user);
        try {

            UserDTO dto = new UserDTO(user);

            if( dto.isValid() ) {
                UserDao dao = new UserDao();

                boolean username = dao.queryByField("username", dto.username).size() != 0;
                boolean git_user = dao.queryByField("gitUsername", dto.gitUsername).size() != 0;

                if (username && git_user)
                    throw new UserAlreadyExistsException("Username and Git-Username already in use");
                else if(username)
                    throw new UserAlreadyExistsException("Username already in use");
                else if(git_user)
                    throw new UserAlreadyExistsException("Git-Username already in use");

                return dao.createIfNotExists(user);
            } else
                throw new BadRequestException("Missing fields in created user.");

        } catch (ConnectException e) {
            L.e("Database service is ofline");
            throw new DatabaseOfflineException();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public User getOwnUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session) {
        User caller = getUserFromSession( session );
        L.i("Called");
        return caller;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public User getUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable Long id)
    {
        getUserFromSession( session );
        L.i("Called");
        try {
            return new UserDao().queryById(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public User updateOwnUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable long id,
                              @RequestBody User userWithUpdateFields)
    {
        L.i("Called");

        if(userWithUpdateFields == null) {
            L.w("Update user received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }

        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

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

        // TODO: 16-5-2016 all fields?

        try {
            int updated = new UserDao().update(caller);
            if(updated != 1) throw new KnownInternalServerError("update own user failed. updated rows = %d", updated);
            return caller;
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                          @PathVariable long id)
    {
        L.i("Called");

        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

        try {
            int deleted = new UserDao().delete(caller);
            if (deleted != 1) throw new KnownInternalServerError("delete user failed. deleted rows = %d", deleted);
            return new HashMap<>();
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }

    }

    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Set<Project> getProjects(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                     @PathVariable Long id)
    {
        getUserFromSession( session );
        L.i("Called");
        try {
            return new UserDao().queryById(id).getProjects().stream()
                    .map( p -> {
                        try {
                            return new ProjectDao().queryById(p.getId());
                        } catch (ConnectException e) {
                            L.e(e, "Database is offline");
                            throw new DatabaseOfflineException();
                        }
                    }).collect(Collectors.toSet());

        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Set<Push> getPushes(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable Long id)
    {
        getUserFromSession( session );
        L.i("Called");
        try {
            return new UserDao().queryById(id).getPushes().stream()
                    .map( p -> {
                        try {
                            return new PushDao().queryById(p.getId());
                        } catch (ConnectException e) {
                            L.e(e, "Database is offline");
                            throw new DatabaseOfflineException();
                        }
                    }).collect(Collectors.toSet());
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getCommits(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                   @PathVariable Long id)
    {
        L.i("Called");
        // TODO : 1 -> check if session is valid, 2 -> get a list of commits from the user id
        throw new UnsupportedOperationException("This will return an list containing all commits under the user");
    }

    @RequestMapping(value = "{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getIssues(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @PathVariable Long id)
    {
        L.i("Called");
        // TODO : 1 -> check if session is valid, 2 -> get a list of issues linked to the user id
        throw new UnsupportedOperationException("This will return an list containing all issues the user created");
    }

    @RequestMapping(value = "{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplications(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable Long id)
    {
        L.i("Called");
        // TODO : 1 -> check if session is valid, 2 -> get a list of duplications with files linked to the user id
        throw new UnsupportedOperationException("This will return an list containing all duplications the user created");
    }

    @RequestMapping(value = "{id}/businesses", method = RequestMethod.GET)
    public Set<Business> getBusinesses(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                       @PathVariable Long id)
    {
        L.i("Called");
        // TODO : 1 -> check if session is valid, 2 -> get a list of Businesses with files linked to the user id
        throw new UnsupportedOperationException("This will return an list containing all duplications the user created");
    }
}
