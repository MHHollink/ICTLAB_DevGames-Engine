package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.Push;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/pushes")
public class PushController extends BaseController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Push getPushById(@RequestHeader(value = Application.SESSION_HEADER_KEY) String session,
                            @PathVariable long id)
    {
        throw new UnsupportedOperationException();
    }
}
