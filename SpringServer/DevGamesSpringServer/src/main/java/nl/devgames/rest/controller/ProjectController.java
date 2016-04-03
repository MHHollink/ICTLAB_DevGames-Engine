package nl.devgames.rest.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;

import java.util.List;

/**
 * Created by Marcel on 31-3-2016.
 */
public class ProjectController extends BaseController{

    /**
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

        String stringResponse = Neo4JRestService.getInstance().postQuery(
                "MATCH (u:User { username : '%s' }) -[:is_developing]-> (p:Project {name : '%s'}) <-[:is_developing]- (r:User) RETURN r.gcmRegId",
                username,
                projectName
        );

        JsonObject jsonResponse = new JsonParser().parse(stringResponse).getAsJsonObject();

        if(hasErrors(jsonResponse)) return null;



        return null;
    }

}
