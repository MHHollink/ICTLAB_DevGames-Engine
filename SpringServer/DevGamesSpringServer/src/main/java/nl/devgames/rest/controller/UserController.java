package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.dao.*;
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
import nl.devgames.rest.errors.EntityAlreadyExistsException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
public class UserController extends BaseController {

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<User> getAllUsers(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session){
        getUserFromSession( session );
        L.d("Called");
        try {
            return new UserDao().queryForAll();
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }
    }

    /**
     * creates a new user
     * @param user      the user to be created
     * @return          the created user with id
     */
    @RequestMapping(method = RequestMethod.POST)
    public User createNewUser(@RequestBody User user) {
        L.d("Called");
        L.t("Creating user: %s", user);
        try {

            UserDTO dto = new UserDTO(user);

            if( dto.isValid() ) {
                UserDao dao = new UserDao();

                boolean username = dao.queryByField("username", dto.username).size() != 0;
                boolean git_user = dao.queryByField("gitUsername", dto.gitUsername).size() != 0;

                if (username && git_user)
                    throw new EntityAlreadyExistsException("Username and Git-Username already in use");
                else if(username)
                    throw new EntityAlreadyExistsException("Username already in use");
                else if(git_user)
                    throw new EntityAlreadyExistsException("Git-Username already in use");

                return dao.createIfNotExists(user);
            } else
                throw new BadRequestException("Missing fields in created user.");

        } catch (ConnectException e) {
            L.e("Database service is ofline");
            throw new DatabaseOfflineException();
        }
    }

    /**
     * gets the user calling
     * @param session       the session id as String
     * @return              the user calling
     */
    @RequestMapping(method = RequestMethod.GET)
    public User getOwnUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session) {
        User caller = getUserFromSession( session );
        L.d("Called");
        return caller;
    }

    /**
     * gets a user by id
     * @param session       the session id as String
     * @param id            the id of the user
     * @return              the user with that id
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public User getUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");
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

    /**
     * updates a user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @param userWithUpdateFields  the user to be updated
     * @return                      the updates user
     */
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public User updateOwnUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable long id,
                              @RequestBody User userWithUpdateFields)
    {
        L.d("Called");

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

        if(userWithUpdateFields.getAge() != null )
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

    /**
     * delets a user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return return               a map with the return message
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                          @PathVariable long id)
    {
        L.d("Called");

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

    /**
     * gets the projects linked to user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return                      a set of projects which the user is developing
     */
    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Set<Project> getProjects(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                     @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");
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

    /**
     * gets the pushes linked to user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return                      a set of pushes which the user pushed
     */
    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Set<Push> getPushes(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");
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

    /**
     * gets the commits linked to user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return                      a set of commits which the user pushed
     */
    @RequestMapping(value = "{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getCommits(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                   @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");

        try {
            return new HashSet<Commit>(new CommitDao().queryByUser(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Commits not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the issues linked to user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return                      a set of issues which the user pushed
     */
    @RequestMapping(value = "{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getIssues(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");

        try {
            return new HashSet<Issue>(new IssueDao().queryByUser(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the duplications linked to user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return                      a set of duplications which the user pushed
     */
    @RequestMapping(value = "{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplications(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");

        try {
            return new HashSet<Duplication>(new DuplicationDao().queryByUser(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Duplications not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the businesses linked to user with id
     * @param session               the session id as String
     * @param id                    the id of the user
     * @return                      a set of businesses which the user pushed
     */
    @RequestMapping(value = "{id}/businesses", method = RequestMethod.GET)
    public Set<Business> getBusinesses(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                       @PathVariable Long id)
    {
        getUserFromSession( session );
        L.d("Called");

        try {
            return new HashSet<Business>(new BusinessDao().queryByUser(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Businesses not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }
}
