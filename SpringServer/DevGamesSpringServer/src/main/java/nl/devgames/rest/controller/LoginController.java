package nl.devgames.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public boolean login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        throw new UnsupportedOperationException("This will return a authtoken");
    }

}
