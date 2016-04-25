package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;

/**
 * BaseController contains every method that all other controllers should have access to.
 */
public abstract class BaseController {

    /**
     * Checking for headers is done via @RequestHeader annotation
     */
    @Deprecated
    protected String getSession(HttpServletRequest request) {
        return request.getHeader(Application.SESSION_HEADER_KEY);
    }

    protected User getUserFromSession(String session) {
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null; // Request to neo4j
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:User) WHERE n.session = '%s' RETURN {id:id(n), labels: labels(n), data: n}",
                    session
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        User user;
        try {
            user = new UserDTO().createFromNeo4jData(
                    UserDTO.findFirst(jsonResponseString)
            ).toModel();
        } catch (IndexOutOfBoundsException e) {
            L.e(e, "Getting user with session '%s' threw IndexOutOfBoundsException, session token was probably invalid", session);
            throw new InvalidSessionException("Request session is not found");
        }
        return user;
    }

    /**
     * Unnecessary method since {@link nl.devgames.connection.database.dto.ModelDTO#getNeo4JData(String)} includes a error check.
     */
    protected boolean hasErrors(JsonObject json) {
        JsonArray errors = json.get("errors").getAsJsonArray(); // get the list of errors

        if (errors.size() != 0) { // Check if there are more the 0 errors
            for (JsonElement error : errors) L.e(error.getAsJsonObject().get("message").getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errors); // throws exception with errors
        }

        return false;
    }

    /**
     * Unnecessary method since {@link nl.devgames.connection.database.dto.ModelDTO#getNeo4JData(String)} does the same but more.
     */
    @Deprecated
    protected JsonArray grabData(String json) {
        JsonObject jsonResponse = new JsonParser().parse(json).getAsJsonObject(); // parse neo4j response

        if(hasErrors(jsonResponse)) return null;

        JsonArray data = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        if(data.size() == 0) throw new InvalidSessionException("Request session is not found");

        return data;
    }
}
