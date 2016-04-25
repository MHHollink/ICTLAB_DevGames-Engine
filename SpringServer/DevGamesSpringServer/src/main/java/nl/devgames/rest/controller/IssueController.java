package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/issues")
public class IssueController extends BaseController{

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Push getIssueById(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                                   @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return Issue with the given id
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Push getPush(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                        @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return push connected to Issue with the given id
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "{id}/user", method = RequestMethod.GET)
    public User getUser(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                        @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return user connected to Issue with the given id
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Project getProject(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                              @PathVariable long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return project connected to Issue with the given id
        throw new UnsupportedOperationException();
    }
}
