package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.User;
import nl.devgames.rest.RestError;
import nl.devgames.rest.RestResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public RestResponse getOwnUser(HttpServletRequest request) {
        String session = request.getHeader(Application.SESSION_HEADER_KEY);

        if(session == null || session.isEmpty()) {
            return RestResponse.error(RestError.INVALID_SESSION);
        }

        /**
         * TODO :
         *
         * - use session to get user
         *   - user = null -> return error
         *   - user != return user
         */

        User user = new User();

        user.setId(1L);
        user.setUsername("admin");
        user.setGitUsername("admin");
        user.setPoints(0);

        return new RestResponse<>(user);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public boolean getUser(HttpServletRequest request, @PathVariable Long id) {
        throw new UnsupportedOperationException("This will return a user with id : " + id);
    }


}
