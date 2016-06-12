package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.CommitDao;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.connection.database.dto.CommitDTO;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.connection.database.dto.PushDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Commit;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;

@RestController
@RequestMapping(value = "/commits")
public class CommitController extends BaseController{

    /**
     * gets a commit by id
     * @param session       the session id as String
     * @param id            the id of the commit
     * @return              the commit with id id
     * @throws ConnectException
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Commit getCommitById(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable long id) throws ConnectException {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        try {
            return new CommitDao().queryById(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Commit was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets pushes of a commit
     * @param session       the session id as String
     * @param id            the id of the commit
     * @return              the push of the commit
     * @throws ConnectException
     */
    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Push getPush(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id) throws ConnectException {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new PushDao().queryByCommit(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Push was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the user of the commit
     * @param session       the session id as String
     * @param id            the id of the commit
     * @return              the user who made the commit
     * @throws ConnectException
     */
    @RequestMapping(value = "{id}/user", method = RequestMethod.GET)
    public User getUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id) throws ConnectException {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new UserDao().queryByCommit(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the project of the commit
     * @param session       the session id as String
     * @param id            the id of the commit
     * @return              the project to which the commit was pushed to
     * @throws ConnectException
     */
    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Project getProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable long id) throws ConnectException {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new ProjectDao().queryByCommit(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }
}
