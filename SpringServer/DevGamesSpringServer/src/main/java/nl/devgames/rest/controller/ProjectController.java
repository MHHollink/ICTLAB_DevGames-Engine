package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController extends BaseController{

    /*
     * Get all project members excluding the user :
     *      <code>
     *          MATCH (u:User { username : 'Marcel' }) -[:is_developing]-> (p:Project) <-[:is_developing]- (r:User) RETURN r
     *      </code>
     *
     * get all project members including the user :
     *      <code>
     *          MATCH (u:User { username : 'Marcel' }) -[:is_developing]-> (p:Project) <-[:is_developing]- (r:User) RETURN r, u
     *      </code>
     *
     * get GCM code for all fellow project members :
     *      <code>
     *           MATCH (u:User { username : 'Marcel' }) -[:is_developing]-> (p:Project {name : 'DevGames'}) <-[:is_developing]- (r:User) RETURN r.gcmRegId
     *      </code>
     */




    public List<String> getProjectMembersTokens(String username, String projectName) {

        List<String> tokenList = new ArrayList<>();

        String stringResponse = null;
        try {
            stringResponse = Neo4JRestService.getInstance().postQuery(
                    "MATCH (u:User { username : '%s' }) -[:is_developing]-> (p:Project {name : '%s'}) <-[:is_developing]- (r:User) RETURN r.gcmRegId",
                    username,
                    projectName
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
            throw new KnownInternalServerError(e.getMessage());
        }

        JsonObject jsonResponse = new JsonParser().parse(stringResponse).getAsJsonObject();

        if(hasErrors(jsonResponse)) return null;

        JsonArray rows = jsonResponse.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        if(rows.size() == 0) return tokenList;

        for (int i = 0; i < rows.size(); i++) {
            tokenList.add(
                    rows.get(i).getAsJsonObject().get("row").getAsJsonArray().get(0).getAsString());
        }

        return tokenList;
    }

}
