package nl.devgames.controllers;

import java.util.concurrent.atomic.AtomicLong;

import devgames.entities.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    //TODO: add Token class
    @RequestMapping("/login")
    public boolean login(@RequestParam(value="username") String username,@RequestParam(value="password") String password) {
        throw new UnsupportedOperationException("This will return a authtoken");
    }
    @RequestMapping("/whoami")
    public User whoAmI(@RequestParam(value="name", defaultValue="World") String name) {
        throw new UnsupportedOperationException("This will return the current logged in user");
    }
    @RequestMapping("/user/{uuid}")
    public User getUserById(@PathVariable String uuid) {
        throw new UnsupportedOperationException("this will return the user by uuid");
    }

}
