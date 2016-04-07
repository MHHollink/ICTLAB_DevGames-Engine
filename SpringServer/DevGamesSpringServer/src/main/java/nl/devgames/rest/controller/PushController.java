package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.model.Push;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcel on 31-3-2016.
 */
public class PushController extends BaseController {

    @RequestMapping(value = "/push", method = RequestMethod.GET)
    public List<Push> getPushes(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                @RequestParam(value = "begin", required = false) Long begin,
                                @RequestParam(value = "end", required = false) Long End) {
        List<Push> responseList = new ArrayList<>();

        // Add all pushes by user between those begin and end.

        return responseList;
    }
}
