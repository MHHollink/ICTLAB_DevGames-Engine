package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.connection.database.dto.CommitDTO;
import nl.devgames.connection.database.dto.DuplicationDTO;
import nl.devgames.connection.database.dto.IssueDTO;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.connection.database.dto.SQReportDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.connection.gcm.GCMMessageComposer;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.rules.ScoreCalculator;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
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
                               @RequestBody String json) {
        L.i("Called");
        Map<String, String> result = new HashMap<>();
        //check if token is valid
        String responseString = null;
        try {
            responseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.token = '%s' " +
                            "RETURN {id:id(n), labels: labels(n), data: n}",
                    token
            );
        } catch (ConnectException e) {
            e.printStackTrace();
        }


        ProjectDTO projectDTO = new ProjectDTO().createFromNeo4jData(
                ProjectDTO.findFirst(responseString)
        );

        if(projectDTO.isValid()) {
            //token is valid
            JsonObject reportAsJson = new JsonParser().parse(json).getAsJsonObject();
            try {
                SQReportDTO testReport = new SQReportDTO().buildFromJson(reportAsJson);
                //todo: get settings for project, temp testing solution below
                File testSettingsFile = new File("settingsTest.txt");
                Scanner scanner = new Scanner(testSettingsFile);
                String settingsAsString = scanner.useDelimiter("\\Z").next();
                scanner.close();
                JsonObject settings = new JsonParser().parse(settingsAsString).getAsJsonObject();
                //calculate score with project settings and report
                testReport.setScore(new ScoreCalculator(settings).calculateScoreFromReport(testReport));
                //save report
                testReport.saveReportToDatabase();


                GCMMessageComposer.sendMessage(
                        GCMMessageType.NEW_PUSH_RECEIVED,
                        "",
                        String.valueOf(testReport.getScore().intValue()),
                        496L
                );
            } catch (Exception e) {
                L.e(e, "Error when parsing report");
                throw new KnownInternalServerError(e.getMessage());
            }
        }
        else {
            //token is not valid
            L.w("token not valid");
            throw new NotFoundException("Project token not found in database");
        }
        return result;
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
        L.i("Called");
        Project returnProject;

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String sessionResponseString = null;
        try {
            sessionResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.session = '%s' " +
                            "RETURN n",
                    session
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }
        String jsonResponseString = null;
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Project { " +
                            "name: '%s', descripion: '%s' }) " +
                            "RETURN {id:id(n), labels: labels(n), data: n}",
                    project.getName(),
                    project.getDescription()
            );
        } catch (ConnectException e) {
            L.e(e, "Cannot create project");
        }
        //session valid
        try {
            returnProject = new ProjectDTO().createFromNeo4jData(
                    ProjectDTO.findFirst(jsonResponseString)
            ).toModel();
        }
        //session invalid
        catch (IndexOutOfBoundsException e) {
            L.e(e, "Getting project with session '%s' threw IndexOutOfBoundsException, session token was probably invalid", session);
            throw new InvalidSessionException("Request session is not found");
        }

        return returnProject;
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
        L.i("Called");
        Project returnProject;
        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN {id:id(n), labels: labels(n), data: n}",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }
        //session valid
        try {
            returnProject = new ProjectDTO().createFromNeo4jData(
                    ProjectDTO.findFirst(jsonResponseString)
            ).toModel();
        }
        //session invalid
        catch (IndexOutOfBoundsException e) {
            L.e(e, "Getting project with session '%s' threw IndexOutOfBoundsException, session token was probably invalid", session);
            throw new InvalidSessionException("Request session is not found");
        }

        return returnProject;
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable(value = "id") long id) throws ConnectException
    {
        L.i("Called");
        Map<String, String> result = new HashMap<>();

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN {id:id(n), labels: labels(n), data: n}",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }
        try {
            Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "DETACH DELETE n",
                    session,
                    id
            );
        }
        catch (IndexOutOfBoundsException e) {
            L.e(e, "Deleting project with session '%s' threw IndexOutOfBoundsException, session token was probably invalid", session);
            throw new InvalidSessionException("Request session is not found");
        }

        return result;
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Project updateProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable(value = "id") long id,
                                 @RequestBody Project projectWithUpdateFields) throws ConnectException
    {
        L.i("Called");
        Project returnProject;
        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN {id:id(n), labels: labels(n), data: n}",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }
        String newProjectResponseString = null;
        try {
            newProjectResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "SET n." +
                            "RETURN n",
                    session,
                    id
            );
            returnProject = new ProjectDTO().createFromNeo4jData(
                    ProjectDTO.findFirst(newProjectResponseString)
            ).toModel();
        }
        catch (IndexOutOfBoundsException e) {
            L.e(e, "Putting project with session '%s' threw IndexOutOfBoundsException, session token was probably invalid", session);
            throw new InvalidSessionException("Request session is not found");
        }

        return returnProject;
        // TODO : 2 -> check if user has update rights for project, 3 -> update project fields
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
        L.i("Called");
        Set<User> returnUserSet = new HashSet<>();

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project), (m:User) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN m-[is_developing]-n",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        try {
            JsonArray results = new JsonParser().parse(jsonResponseString).getAsJsonObject().get("results").getAsJsonArray();
            for(JsonElement element : results) {
                User user = new UserDTO().createFromNeo4jData(
                        ProjectDTO.findFirst(element.getAsString())
                ).toModel();
                returnUserSet.add(user);
            }
        }
        catch (KnownInternalServerError e){
            L.e(e + "Cannot get users working on project with session: '%s' ", session);
            throw new InvalidSessionException("Cannot get users working on project with session");
        }

        return returnUserSet;
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
        L.i("Called");
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
        L.i("Called");
        Set<Issue> returnIssueSet = new HashSet<>();

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            //TODO: get issues of pushes of project with id???
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project), (m:Issue) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN m-[]-n",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        try {
            JsonArray results = new JsonParser().parse(jsonResponseString).getAsJsonObject().get("results").getAsJsonArray();
            for(JsonElement element : results) {
                Issue issue = new IssueDTO().createFromNeo4jData(
                        ProjectDTO.findFirst(element.getAsString())
                ).toModel();
                returnIssueSet.add(issue);
            }
        }
        catch (KnownInternalServerError e){
            L.e(e + "Cannot get users working on project with session: '%s' ", session);
            throw new InvalidSessionException("Cannot get users working on project with session");
        }

        return returnIssueSet;
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
        L.i("Called");
        Set<Duplication> returnDuplicationSet = new HashSet<>();

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            //TODO: get duplications of pushes of project with id???
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project), (m:Duplication) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN m-[]-n",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        try {
            JsonArray results = new JsonParser().parse(jsonResponseString).getAsJsonObject().get("results").getAsJsonArray();
            for(JsonElement element : results) {
                Duplication duplication = new DuplicationDTO().createFromNeo4jData(
                        ProjectDTO.findFirst(element.getAsString())
                ).toModel();
                returnDuplicationSet.add(duplication);
            }
        }
        catch (KnownInternalServerError e){
            L.e(e + "Cannot get users working on project with session: '%s' ", session);
            throw new InvalidSessionException("Cannot get users working on project with session");
        }

        return returnDuplicationSet;
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
        L.i("Called");
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
        L.i("Called");
        Set<Commit> returnCommitSet = new HashSet<>();

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            //TODO: get commits of pushes of project with id???
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project), (m:Commit) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN m-[]-n",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        try {
            JsonArray results = new JsonParser().parse(jsonResponseString).getAsJsonObject().get("results").getAsJsonArray();
            for(JsonElement element : results) {
                Commit commit = new CommitDTO().createFromNeo4jData(
                        ProjectDTO.findFirst(element.getAsString())
                ).toModel();
                returnCommitSet.add(commit);
            }
        }
        catch (KnownInternalServerError e){
            L.e(e + "Cannot get users working on project with session: '%s' ", session);
            throw new InvalidSessionException("Cannot get users working on project with session");
        }

        return returnCommitSet;
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
        L.i("Called");
        Set<Business> returnBusinessSet = new HashSet<>();

        //check if session is valid
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank

        String jsonResponseString = null;
        try {
            //TODO: get businesses of pushes of project with id???
            jsonResponseString = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project), (m:Business) " +
                            "WHERE n.session = '%s' AND ID(n) = '%d' " +
                            "RETURN m-[]-n",
                    session,
                    id
            );
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }

        try {
            JsonArray results = new JsonParser().parse(jsonResponseString).getAsJsonObject().get("results").getAsJsonArray();
            for(JsonElement element : results) {
                Business business = new BusinessDTO().createFromNeo4jData(
                        ProjectDTO.findFirst(element.getAsString())
                ).toModel();
                returnBusinessSet.add(business);
            }
        }
        catch (KnownInternalServerError e){
            L.e(e + "Cannot get users working on project with session: '%s' ", session);
            throw new InvalidSessionException("Cannot get users working on project with session");
        }

        return returnBusinessSet;
    }
}
