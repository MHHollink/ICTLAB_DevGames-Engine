package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.model.Business;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;

/**
 * The Bussiness controller contains all rest request used on the `business` resource.
 *      Employees and projects linked to a business can be asked for.
 */
@RestController
@RequestMapping(value = "/businesses")
public class BusinessController extends BaseController{

    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Business getCallersBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        L.d("Called");
        return getBusiness( session );
    }

    /**
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "{id}/users", method = RequestMethod.PUT)
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
    @RequestMapping(value = "{id}/users", method = RequestMethod.GET)
    public Business getEmployeeFromCallersBusiness(@RequestHeader(Application.SESSION_HEADER_KEY) String session) {
        getUserFromSession( session );
        L.d("Called");
        throw new UnsupportedOperationException("Method call not implemented yet. Shall get all employees of a Business");
    }





    /**
     *
     * @param session
     * @return
     */
    private Business getBusiness(String session) {
        String json = null;
        try {
            json = Neo4JRestService.getInstance().postQuery(
                    "MATCH (u:User { session : '%s' }) <-[:has_employee]- (b:Business) RETURN {id:id(b), labels: labels(b), data: b}",
                    session
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
            throw new KnownInternalServerError(e.getMessage());
        }

        return new BusinessDTO().createFromJsonObject(
                grabData(json).get(0).getAsJsonObject().get("row").getAsJsonArray().get(0).getAsJsonObject()
        ).toModel(); // Returns business object
    }
}
