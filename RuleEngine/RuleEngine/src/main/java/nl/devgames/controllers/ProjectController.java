package nl.devgames.controllers;

import devgames.entities.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wouter on 3/5/2016.
 */
@RestController
public class ProjectController {
    @RequestMapping("/projects/{uuid}")
    public User getProjectsByUserId(@PathVariable String uuid) {
        throw new UnsupportedOperationException();
    }
}
