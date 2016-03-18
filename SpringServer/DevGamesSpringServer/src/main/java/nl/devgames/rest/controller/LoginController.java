package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.rest.errors.BadRequestException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String,String> login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        if (password == null || password.isEmpty() || username == null || username.isEmpty())
            throw new BadRequestException("Username or password was missing");

        username = username.toLowerCase();

        // TODO, check db for existing users
        if(username.equalsIgnoreCase("marcel") && password.equals("DevGames")) {

            java.util.Map<String, String> result = new java.util.HashMap<>();

            // TODO, user some real session management stuff
            String sessionID = String.valueOf(UUID.randomUUID());

            result.put(Application.SESSION_HEADER_KEY, sessionID );

            return result;

        } else {
            throw new BadRequestException("This username-password combination is not found");
        }
    }

}
