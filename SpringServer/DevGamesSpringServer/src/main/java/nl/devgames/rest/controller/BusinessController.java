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

@RestController
public class BusinessController extends BaseController{

    /**
     * Get all colleagues of a user :
     *      <code>
     *          MATCH (u:User { username : 'Marcel' }) <-[:has_employee]- (b:Business) -[:has_employee]-> (r:User) return r
     *      </code>
     *
     */

    @RequestMapping(value = "/business/{id}", method = RequestMethod.GET)
    public Business getBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        L.og("Called");
        return new Business().createFromJsonObject( getBusinessJsonFromRequest( session ) );
    }


    @RequestMapping(value = "/business/{id}/user", method = RequestMethod.PUT)
    public Business addEmployeeToBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        getUserFromSession( session );
        L.og("Called");
        throw new UnsupportedOperationException("Method call not implemented yet. Shall add user to Business");
    }





    private JsonObject getBusinessJsonFromRequest(String session) {

        User user = getUserFromSession(session);



        return null;
    }
}
