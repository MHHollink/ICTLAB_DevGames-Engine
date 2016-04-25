package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/pushes")
public class PushController extends BaseController {

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Push getPushById(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                            @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return push with the given id
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
    public User getPushPusher(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                              @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return the user who created the push
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}/projects", method = RequestMethod.GET)
    public Project getProjectFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                           @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return the project that the push is pushed to
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getUnderLayingCommits(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                             @PathVariable long id) {
        // TODO : 1 -> check if session is valid, 2 -> return a set containing all commits connected to the push
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getIssuesFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                        @PathVariable long id) {
        // TODO : 1 -> check if session is valid, 2 -> return a set containing all issues connected to the push
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplicationsFromPush(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                                    @PathVariable long id) {
        // TODO : 1 -> check if session is valid, 2 -> return a set containing all duplications (and files) connected to the push
        throw new UnsupportedOperationException();
    }
}
