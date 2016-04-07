package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.Push;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/pushes")
public class PushController extends BaseController {

    @RequestMapping(method = RequestMethod.GET)
    public List<Push> getPushes(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @RequestParam(value = "begin", required = false) Long begin,
                                @RequestParam(value = "end", required = false) Long End) {
        List<Push> responseList = new ArrayList<>();

        // Add all pushes by user between those begin and end.

        return responseList;
    }
}
