package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;

import javax.servlet.http.HttpServletRequest;

/**
 * BaseController contains every method that all other controllers should have access to.
 */
public abstract class BaseController {

    @Deprecated
    protected String getSession(HttpServletRequest request) {
        return request.getHeader(Application.SESSION_HEADER_KEY);
    }

    protected User getUserFromSession(String session) {
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) WHERE n.session = '%s' RETURN {id:id(n), labels: labels(n), data: n}",
                session
        ); // Request to neo4j

        JsonObject jsonResponse = new JsonParser().parse(jsonResponseString).getAsJsonObject(); // parse neo4j response

        if(hasErrors(jsonResponse)) return null;

        JsonArray data = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        if(data.size() == 0) throw new InvalidSessionException("Request session is not found");

        return new User().createFromJsonObject(
                data.get(0).getAsJsonObject().get("row").getAsJsonArray().get(0).getAsJsonObject()
        ); // Returns user object
    }

    protected boolean hasErrors(JsonObject json) {
        JsonArray errors = json.get("errors").getAsJsonArray(); // get the list of errors

        if (errors.size() != 0) { // Check if there are more the 0 errors
            for (JsonElement error : errors) L.og(error.getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errors.getAsString()); // throws exception with errors
        }

        return false;
    }
}
