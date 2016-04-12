package nl.devgames.rest.controller;

import com.google.gson.JsonObject;
import nl.devgames.Application;
import nl.devgames.model.Business;
import nl.devgames.model.User;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * The Bussiness controller contains all rest request used on the `business` resource.
 *      Employees and projects linked to a business can be asked for.
 */
@RestController
@RequestMapping(value = "/businesses")
public class BusinessController extends BaseController{

    /*
     * Get all colleagues of a user :
     *      <code>
     *          MATCH (u:User { username : 'Marcel' }) <-[:has_employee]- (b:Business) -[:has_employee]-> (r:User) return r
     *      </code>
     *
     */


    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Business getBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        L.d("Called");
        return new Business().createFromJsonObject( getBusinessJsonFromRequest( session ) );
    }


    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "{id}/user", method = RequestMethod.PUT)
    public Business addEmployeeToBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        getUserFromSession( session );
        L.d("Called");
        throw new UnsupportedOperationException("Method call not implemented yet. Shall add user to Business");
    }





    /**
     *
     * @param session
     * @return
     */
    private JsonObject getBusinessJsonFromRequest(String session) {
        User user = getUserFromSession(session);

        return null;
    }
}
