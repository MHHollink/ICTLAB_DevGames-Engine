package nl.devgames.rest.controller;

import com.google.gson.JsonObject;
import nl.devgames.Application;
import nl.devgames.model.Business;
import nl.devgames.model.User;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BusinessController {

    @RequestMapping(value = "/business/{id}", method = RequestMethod.GET)
    public Business getBusiness(HttpServletRequest request) {
        L.og("Called");
        return new Business().createFromJsonObject( getBusinessJsonFromRequest( request ) );
    }








    private JsonObject getBusinessJsonFromRequest(HttpServletRequest request) {

        User user = AuthController.getUserFromSession(request.getHeader(Application.SESSION_HEADER_KEY));



        return null;
    }
}
