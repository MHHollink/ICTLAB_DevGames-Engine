package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.UserDao;
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
import nl.devgames.rest.errors.*;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
     */

     /**
     * @param token
     * @param json
     * @return
     *
     */
    @RequestMapping(value = "/{token}/build", method = RequestMethod.POST)
    public Map startCalculator(@PathVariable("token") String token,
                               @RequestBody String json) throws ConnectException {
        java.util.Map<String, String> result = new java.util.HashMap<>();

        List<Project> projects = new ProjectDao().queryByField("token", token);
        //check if token is invalid
        if(projects.get(0)==null)
            throw new NotFoundException("project with token not found!");

        L.i("Called");
        try {
            //parse build as SQReportDTO
            JsonObject reportAsJson = new JsonParser().parse(json).getAsJsonObject();
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
            //send message
            GCMMessageComposer.sendMessage(
                    GCMMessageType.NEW_SCORES,
                    "",
                    String.valueOf(testReport.getScore().intValue()),
                    496L
            );

            result.put("message", "successfully parsed and saved report");
        }catch (Exception e) {
            L.e(e, "Error when parsing report");
            throw new KnownInternalServerError(e.getMessage());
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
                                 @RequestBody Project project) throws ConnectException {
        //check if session is valid
        User caller = getUserFromSession( session );
        L.i("Called");
        try {
            return new ProjectDao().createIfNotExists(project);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException("Database service offline!");
        }
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
        //check if session is valid
        User caller = getUserFromSession( session );
        L.i("Called");
        try {
            return new ProjectDao().queryForId(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException("Database service offline!");
        } catch (IndexOutOfBoundsException e) {
            L.w("Project was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteProject(@RequestHeader(Application.SESSION_HEADER_KEY) String session,
                                 @PathVariable(value = "id") long id) throws ConnectException {
        java.util.Map<String, String> result = new java.util.HashMap<>();

        L.i("Called");
        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for project with id '%d'", id );

        try {
            ProjectDao projectDao = new ProjectDao();
            Project project = projectDao.queryForId(id);
            int deleted = projectDao.delete(project);
            if (deleted != 1) throw new KnownInternalServerError("delete project failed. deleted rows = %d", deleted);
            result.put("message", "succesfully deleted project");
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException("Database service offline!");
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
                                 @RequestBody Project projectWithUpdateFields) throws ConnectException {
        L.i("Called");

        if(projectWithUpdateFields == null) {
            L.w("Update project received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

        //check if user has update rights for project
        //TODO: check if user has update rights
        Project project = new ProjectDao().queryForId(id);

        //update project fields
        if(projectWithUpdateFields.getName() != null)
            project.setName(projectWithUpdateFields.getName());

        if(projectWithUpdateFields.getDescription() != null)
            project.setDescription(projectWithUpdateFields.getDescription());

        // TODO: 17-5-2016 all fields?

        //update in db
        try {
            int updated = new ProjectDao().update(project);
            if(updated != 1) throw new KnownInternalServerError("update project failed. updated rows = %d", updated);
            return project;
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException("Database service offline!");
        }

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

        //check if session is valid
        User caller = getUserFromSession( session );
        if(caller.getId() != id) throw new BadRequestException( "Session does not match session for user with id '%d'", id );

        try {
            return new UserDao().(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException("Database service offline!");
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
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
