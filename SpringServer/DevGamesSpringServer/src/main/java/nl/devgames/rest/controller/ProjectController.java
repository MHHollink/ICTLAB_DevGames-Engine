package nl.devgames.rest.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.dao.BusinessDao;
import nl.devgames.connection.database.dao.CommitDao;
import nl.devgames.connection.database.dao.DuplicationDao;
import nl.devgames.connection.database.dao.IssueDao;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.connection.database.dto.SQReportDTO;
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
import nl.devgames.rest.errors.DatabaseOfflineException;
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
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

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
                               @RequestBody String json) {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        try {
            Project project = new ProjectDao().queryByField("token", token).get(0);
            //check if token is invalid
            if (project == null)
                throw new NotFoundException("project with token not found!");

        } catch (ConnectException e) {
            L.e("Database is offline");
            throw new DatabaseOfflineException();
        }
        try {
            //parse build as SQReportDTO
            JsonObject reportAsJson = new JsonParser().parse(json).getAsJsonObject();
            SQReportDTO testReport = new SQReportDTO().buildFromJson(reportAsJson, token);
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
            //get sender(s)
            User author = testReport.getAuthor();
            //send message
            GCMMessageComposer.sendMessage(
                    GCMMessageType.NEW_PUSH_RECEIVED,
                    "",
                    String.valueOf(testReport.getScore().intValue()),
                    author.getId()
            );

            result.put("message", "successfully parsed and saved report");
        } catch (ConnectException e) {
            L.e(e, "Database offline");
            throw new DatabaseOfflineException();
        } catch (FileNotFoundException ignored) {}

        return result;
    }

    /**
     *
     * @param session
     * @param project
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Project createProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @RequestBody Project project) {
        L.d("Called");
        //check if session is valid
        User caller = getUserFromSession( session );

        if (project.getName() == null) {
            throw new BadRequestException("Missing name project body 'Project{name: ?}'");
        }
        if (project.getName().length() < 8) {
            throw new BadRequestException("Project name must at least be 8 characters long!");
        }

        try {
            //set owner
            project.setOwner(caller);
            project.setToken(UUID.randomUUID().toString());

            ProjectDao dao = new ProjectDao();
            Project returnProject = dao.createIfNotExists(project);
            //set creator of project
            dao.saveRelationship(returnProject, caller);
            //also add user working on
            new UserDao().saveRelationship(caller, returnProject);
            return returnProject;
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Project getProjectById(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                  @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new ProjectDao().queryById(id);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
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
    public Map deleteProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @PathVariable(value = "id") long id) {
        L.d("Called");
        //todo delete everything

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            ProjectDao projectDao = new ProjectDao();
            Project project = projectDao.queryById(id);
            int deleted = projectDao.delete(project);
            if (deleted != 1) throw new KnownInternalServerError("delete project failed. deleted rows = %d", deleted);
            result.put("message", "succesfully deleted project");
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
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
    public Project updateProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @PathVariable(value = "id") long id,
                                 @RequestBody Project projectWithUpdateFields) {
        L.d("Called");

        if(projectWithUpdateFields == null) {
            L.w("Update project received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }

        //check if session is valid
        User caller = getUserFromSession( session );

        //check if user has update rights for project
        //todo  check if user has update rights for project

        Project project = null;
        try {
            project = new ProjectDao().queryById(id);

            //update project fields
            if(projectWithUpdateFields.getName() != null)
                project.setName(projectWithUpdateFields.getName());

            if(projectWithUpdateFields.getDescription() != null)
                project.setDescription(projectWithUpdateFields.getDescription());

        } catch (ConnectException e) {
            L.e("database is offline");
            throw new DatabaseOfflineException();
        }
        // TODO: 17-5-2016 all fields?

        //update in db
        try {
            int updated = new ProjectDao().update(project);
            if(updated != 1) throw new KnownInternalServerError("update project failed. updated rows = %d", updated);
            return project;
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }

    }


    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/users", method = RequestMethod.GET)
    public Set<User> getDevelopersFromProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                              @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<User>(new UserDao().queryByProject(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Users were not found");
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
    public Map addDeveloperToProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                         @PathVariable(value = "id") long id,
                                         @PathVariable(value = "uid") long uid) {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );

        //check if user has update rights for project
        //todo  check if user has update rights for project
        //add user to project
        try {
            int updated = new ProjectDao().addUserToProject(uid, id);
            result.put("message", String.format("succesfully updated %d user(s)", updated));
            return result;
        } catch (ConnectException e) {
            L.e(e, "database is offline!");
            throw new DatabaseOfflineException();
        }
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/issues", method = RequestMethod.GET)
    public Set<Issue> getIssuesFromProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                           @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<Issue>(new IssueDao().queryFromProject(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }

    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/duplications", method = RequestMethod.GET)
    public Set<Duplication> getDuplicationsFromProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                                       @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<Duplication>(new DuplicationDao().queryFromProject(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Set<Push> getPushesFromProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                          @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<Push>(new PushDao().queryFromProject(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/commits", method = RequestMethod.GET)
    public Set<Commit> getCommitsFromProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                             @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<Commit>(new CommitDao().queryFromProject(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    /**
     *
     * @param session
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}/businesses", method = RequestMethod.GET)
    public Set<Business> getBusinessesFromProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                            @PathVariable(value = "id") long id)
    {
        L.d("Called");

        //check if session is valid
        User caller = getUserFromSession( session );

        try {
            return new HashSet<Business>(new BusinessDao().queryFromProject(id));
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }



    // todo move method to more appropriate class
    public List<String> getProjectMembersTokens(long uid, long projectId) {
        try {
            return new UserDao().userTokensFromProject(uid, projectId);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("Issues not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

}
