package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.Business;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Business createBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                   @RequestBody Business business)
    {
        // TODO : 1 -> check if session is valid, 2 -> create a business from given object 3 -> link user to it
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Business getBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> get business with id if user is linked to it
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                              @PathVariable(value = "id") long id)
    {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/users", method = RequestMethod.GET)
    public Set<User> getEmployees(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                                    @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return list of all users in relation with this business
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @param uid
     * @return
     */
    @RequestMapping(value = "{id}/users/{uid}", method = RequestMethod.PUT)
    public Map addEmployeeToBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                          @PathVariable(value = "id") long id,
                                          @PathVariable(value = "uid") long uid)
    {
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
    public Set<Project> getProjects(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                    @PathVariable(value = "id") long id)
    {
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
    public Map addProjectToBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                         @PathVariable(value = "id") long id,
                                         @PathVariable(value = "pid") long pid)
    {
        // TODO : 1 -> check if session is valid, 2 -> add a relation between nodes
        throw new UnsupportedOperationException();
    }

}
