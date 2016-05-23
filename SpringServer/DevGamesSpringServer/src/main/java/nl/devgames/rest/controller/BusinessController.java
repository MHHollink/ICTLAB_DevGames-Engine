package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.BusinessDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.DatabaseOfflineException;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Bussiness controller contains all rest request used on the `business` resource.
 *      Employees and projects linked to a business can be asked for.
 */
// "MATCH (u:User { session : '%s' }) <-[:has_employee]- (b:Business) RETURN {id:id(b), labels: labels(b), data: b}"
@RestController
@RequestMapping(value = "/businesses")
public class BusinessController extends BaseController{

    @RequestMapping(method = RequestMethod.POST)
    public Business createBusiness(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                   @RequestBody Business business) throws ConnectException {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new BusinessDao().createIfNotExists(business);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }
    }

    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Business getBusiness(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable(value = "id") long id) throws ConnectException {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession(session);

        try {
            return new BusinessDao().queryById(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Business was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteBusiness(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable(value = "id") long id)
    {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            BusinessDao businessDao = new BusinessDao();
            Business business = businessDao.queryById(id);
            int deleted = businessDao.delete(business);
            if (deleted != 1) throw new KnownInternalServerError("delete business failed. deleted rows = %d", deleted);
            result.put("message", "succesfully deleted business");
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }
        return result;
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/users", method = RequestMethod.GET)
    public Set<User> getEmployees(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                                    @PathVariable(value = "id") long id)
    {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<User>(new UserDao().queryByBusiness(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Users were not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     *
     * @param session
     * @param id
     * @param uid
     * @return
     */
    @RequestMapping(value = "{id}/users/{uid}", method = RequestMethod.PUT)
    public Map addEmployeeToBusiness(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                          @PathVariable(value = "id") long id,
                                          @PathVariable(value = "uid") long uid)
    {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );


        // TODO : 1 -> check if session is valid, 2 -> add a relation between nodes
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Set<Project> getProjects(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                    @PathVariable(value = "id") long id)
    {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );


        // TODO : 1 -> check if session is valid, 2 -> return all projects that have a relation
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @param pid
     * @return
     */
    @RequestMapping(value = "{id}/projects/{pid}", method = RequestMethod.PUT)
    public Map addProjectToBusiness(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                         @PathVariable(value = "id") long id,
                                         @PathVariable(value = "pid") long pid)
    {
        // TODO : 1 -> check if session is valid, 2 -> add a relation between nodes
        throw new UnsupportedOperationException();
    }

}
