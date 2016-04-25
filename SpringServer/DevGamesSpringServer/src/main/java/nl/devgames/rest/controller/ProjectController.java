package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.SQReportDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController extends BaseController{
    /**
     * Example :
     *
     * <code>
     *  {
     *      projectName: "DevGames",
     *      result: "SUCCESS",
     *      timestamp: 123456,
     *      author: "Evert-Jan Heilema",
     *      items: [
     *          {
     *              commitId: "81f5429a5145a25001ae2510ad860d04e11a8460",
     *              commitMsg: "remove test files",
     *              timestamp: "1234567"
     *          },
     *          {
     *              commitId: "879a0ba220f5c1441cfc6ac5d8d521f5df0dfee7",
     *              commitMsg: "folders",
     *              timestamp: "1234567"
     *          }
     *      ],
     *      issues: [
     *          {
     *              severity: "MAJOR",
     *              component: "nl.evertjanheilema.testprojects:TestProject:src/main/java/Application.java",
     *              startLine: 7,
     *              endLine: 20,
     *              status: "OPEN",
     *              resolution: "FIXED",
     *              message: "Add a private constructor to hide the implicit public one.",
     *              debt: "30min",
     *              creationDate: "2345678",
     *              updateDate: "2345678",
     *              closeDate: "2345678"
     *          }
     *      ],
     *      duplications: [
     *          {
     *              files: [
     *                  {
     *                      file: "src/nl/evertjanheilema/Main.java",
     *                      beginLine: 119,
     *                      endLine: 125,
     *                      size: 15
     *                  },
     *                  {
     *                      file: "src/nl/evertjanheilema/Controllers/MainScreenController.java",
     *                      beginLine: 221,
     *                      endLine: 127,
     *                      size: 21
     *                  }
     *              ]
     *          }
     *      ]
     *  }
     * </code>
     *
     * @param token
     * @param json
     * @return
     *
     */
    @RequestMapping(value = "/{token}/build", method = RequestMethod.POST)
    public Map startCalculator(@PathVariable("token") String token,
                               @RequestBody String json)
    {
        // TODO : 1 -> check if token is valid, 2 ->  parse and calculate, 3 -> save
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param project
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Project createProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @RequestBody Project project)
    {
        // TODO : 1 -> check if session is valid, 2 -> Create project node in database, 3 -> return saved project
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Project getProjectById(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                  @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return project with id
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable(value = "id") long id)
    {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/users/{uid}", method = RequestMethod.PUT)
    public Project updateProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> check if user has update rights for project, 3 -> update project fields
        throw new UnsupportedOperationException();
    }


    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/users", method = RequestMethod.GET)
    public Set<User> getDevelopersFromProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                              @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 -> return list of developers from project
        throw new UnsupportedOperationException();
    }


    /**
     *
     * @param session
     * @param id
     * @param uid
     * @return
     */
    @RequestMapping(value = "{id}/users/{uid}", method = RequestMethod.PUT)
    public Map addDeveloperToProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                         @PathVariable(value = "id") long id,
                                         @PathVariable(value = "uid") long uid)
    {
        // TODO : 1 -> check if session is valid, 2 -> check if user has rights to add new users to project, 3 -> add user to project
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getIssuesFromProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                           @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 ->  return list of issues linked to the project
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplicationsFromProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                                       @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 ->  return list of duplications linked to the project
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Set<Push> getPushesFromProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                          @PathVariable(value = "id") long id)
    {

        // TODO : 1 -> check if session is valid, 2 ->  return list of pushes (includes commits, issues and duplications) linked to the project
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getCommitsFromProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                             @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 ->  return list of commits linked to the project
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/businesses", method = RequestMethod.GET)
    public Set<Business> getBusinessesFromProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                            @PathVariable(value = "id") long id)
    {
        // TODO : 1 -> check if session is valid, 2 ->  return list of businesses linked to the project
        throw new UnsupportedOperationException();
    }



    // todo move method to more appropriate class
    public List<String> getProjectMembersTokens(String username, String projectName) {

        List<String> tokenList = new ArrayList<>();

        String stringResponse;
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

        // todo use UserDTOs parse method
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
