package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.dao.DuplicationDao;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.model.Duplication;
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
@RequestMapping(value = "/duplications")
public class DuplicationController extends BaseController {

    /**
     * gets a duplication by id
     * @param session       the session id as String
     * @param id            the id of the duplication
     * @return              the duplication with id id
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Duplication getDuplicationById(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                          @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new DuplicationDao().queryById(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Duplication was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the push of a duplication
     * @param session       the session id as String
     * @param id            the id of the duplication
     * @return              the push related to the duplication
     */
    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Push getPush(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new PushDao().queryByDuplication(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Push was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the user who pushed the duplication
     * @param session       the session id as String
     * @param id            the id of the duplication
     * @return                 the user who pushed the duplication
     */
    @RequestMapping(value = "{id}/user", method = RequestMethod.GET)
    public User getUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new UserDao().queryByDuplication(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     * gets the project of a duplication
     * @param session       the session id as String
     * @param id            the id of the duplication
     * @return              the project to which the duplication was pushed to
     */
    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Project getProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new ProjectDao().queryByDuplication(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }
}
