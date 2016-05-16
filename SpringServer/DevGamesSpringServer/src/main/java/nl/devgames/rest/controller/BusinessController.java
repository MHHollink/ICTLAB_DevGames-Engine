package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import nl.devgames.rest.errors.InvalidSessionException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
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
                                   @RequestBody Business business) throws ConnectException {
        Business returnBusiness = new Business();

        //create business
        String businessResponseString = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Business { " +
                        "name: '%s' }) " +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                business.getName()
        );
        //TODO: get user
        User user = new User();

        //link user to business
        String userLinkResponseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Business { name:'%s' }), (m:User { id:'%d' }) " +
                        "CREATE (n)-[:has_employee]->(m)",
                business.getName(),
                user.getId()
        );

        BusinessDTO businessDTO = new BusinessDTO().createFromNeo4jData(
                BusinessDTO.findFirst(businessResponseString)
        );
        if(businessDTO.isValid()) {
            returnBusiness = businessDTO.toModel();
        }
        else {
            throw new InvalidSessionException("Request session is not found");
        }
        return returnBusiness;
    }

    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Business getBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @PathVariable(value = "id") long id) throws ConnectException {
        Business returnBusiness = new Business();

        //get business with id if user is linked to it
        String businessResponseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Business { id:'%d' })<-[:pushed_by]-(p:User { sessionId:'%s' })" +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id,
                session
        );
        BusinessDTO businessDTO = new BusinessDTO().createFromNeo4jData(BusinessDTO.findFirst(businessResponseString));
        if(businessDTO.isValid()) {
            //session valid and business has user
            returnBusiness = businessDTO.toModel();
        }

        return returnBusiness;
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
