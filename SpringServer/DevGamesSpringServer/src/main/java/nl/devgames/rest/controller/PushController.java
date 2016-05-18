package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.dao.*;
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

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Push getPushById(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                            @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

        try {
            return new PushDao().queryForId(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
    public User getPushPusher(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                              @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

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

    @RequestMapping(value = "/{id}/projects", method = RequestMethod.GET)
    public Project getProjectFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                           @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

        try {
            return new ProjectDao().getProjectForPush(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "/{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getUnderLayingCommits(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                             @PathVariable long id) {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

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

    @RequestMapping(value = "/{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getUnderLayingIssues(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                        @PathVariable long id) {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

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

    @RequestMapping(value = "/{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplicationsFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                                    @PathVariable long id) {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

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
