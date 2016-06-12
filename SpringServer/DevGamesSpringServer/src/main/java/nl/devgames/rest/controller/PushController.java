package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.dao.CommitDao;
import nl.devgames.connection.database.dao.DuplicationDao;
import nl.devgames.connection.database.dao.IssueDao;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/pushes")
public class PushController extends BaseController {

    /**
     * gets a push by id
     * @param session       the session of the user calling the method as String
     * @param id            the id of the push to get
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Push getPushById(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                            @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new PushDao().queryById(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the user who pushed the push
     * @param session       the session of the user calling the method as String
     * @param id            the id of the push
     * @return              the user who pushed the push
     */
    @RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
    public User getPushPusher(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new UserDao().getPusherOfPush(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the project to which the push was pushed
     * @param session       the session of the user calling the method as String
     * @param id            the id of the push
     * @return              the project to which the push was pushed
     */
    @RequestMapping(value = "/{id}/projects", method = RequestMethod.GET)
    public Project getProjectFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                           @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new ProjectDao().getProjectByPush(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the commits of the push
     * @param session       the session of the user calling the method as String
     * @param id            the id of the push
     * @return              the commits in relation to the push
     */
    @RequestMapping(value = "/{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getUnderLayingCommits(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                             @PathVariable long id) {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<>(new CommitDao().getCommitsFromPush(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the issues of the push
     * @param session       the session of the user calling the method as String
     * @param id            the id of the push
     * @return              the issues in relation to the push
     */
    @RequestMapping(value = "/{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getUnderLayingIssues(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                        @PathVariable long id) {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<>(new IssueDao().getIssuesFromPush(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the duplications of the push
     * @param session       the session of the user calling the method as String
     * @param id            the id of the push
     * @return              the duplications in relation to the push
     */
    @RequestMapping(value = "/{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplicationsFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                                    @PathVariable long id) {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<>(new DuplicationDao().getDuplicationsFromPush(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }
}
