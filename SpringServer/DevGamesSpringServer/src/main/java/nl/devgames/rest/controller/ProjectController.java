package nl.devgames.rest.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.dao.*;
import nl.devgames.connection.database.dto.SQReportDTO;
import nl.devgames.connection.gcm.GCMMessageComposer;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.model.*;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.EntityAlreadyExistsException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.rules.AchievementManager;
import nl.devgames.rules.IssueScoreCalculator;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

     /**parses an incoming build report, saves it and sends a message to the user of the sent report
     * @param token     project token as String
     * @param json      report data as string
     * @return          a Map containing the return message
     *
     */
    @RequestMapping(value = "/{token}/build", method = RequestMethod.POST)
    public Map startCalculator(@PathVariable("token") String token,
                               @RequestBody String json) {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        Project project;
        try {
            project = new ProjectDao().queryByField("token", token).get(0);
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
            SQReportDTO report = new SQReportDTO().buildFromJson(reportAsJson, token);

            //get settings and calculate score
            Settings settings = new SettingsDao().queryByProject(project.getId());
            report.setScore(
                    new IssueScoreCalculator(settings).calculateScoreFromReport(report)
            );

            report.saveReportToDatabase();

            User author = report.getAuthor();

            L.i("Sending GCM message to %s", author.getUsername());
            GCMMessageComposer.sendMessage(
                    GCMMessageType.NEW_PUSH_RECEIVED,
                    "",
                    String.valueOf(report.getScore().intValue()),
                    author.getId()
            );

            result.put("message", "successfully parsed and saved report");

            //start checking for achievements of user
            new AchievementManager(author).checkAchievementsOfUser();
        } catch (ConnectException e) {
            L.e(e, "Database offline");
            throw new DatabaseOfflineException();
        }

        return result;
    }

    /**creates a project
     *
     * @param session       the session of the user calling the method as String
     * @param project       the Project to be created
     * @return              the project created
     */
    @RequestMapping(method = RequestMethod.POST)
    public Project createProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @RequestBody Project project) {
        L.d("Called");
        User caller = getUserFromSession( session ); // contains session valid check

        if (project.getName() == null) {
            L.w("Project not created, no name supplied");
            throw new BadRequestException("Missing name project body 'Project{name: ?}'");
        }
        if (project.getName().length() < 8) {
            L.w("Project not created, name length to short");
            throw new BadRequestException("Project name must at least be 8 characters long!");
        }


        try {
            ProjectDao projectDAO   = new ProjectDao();
            boolean projectExist = projectDAO.queryByField("name", project.getName()).size() != 0;
            if(projectExist)
                throw new EntityAlreadyExistsException("Project name already found");

            L.d("Creating project: '%s'", project.getName());
            UserDao    userDAO      = new UserDao();

            project.setToken(UUID.randomUUID().toString());
            project = projectDAO.createIfNotExists(project);

            Settings s = new Settings();
            s.setDefault();
            s = new SettingsDao().createIfNotExists(s); // Set default and create settings
            projectDAO.saveRelationship(project, s); // Set settings as project settings

            projectDAO.saveRelationship(project, caller); // Set caller as project owner
            userDAO.saveRelationship(caller, project);    // Set caller as developer in project

            return projectDAO.queryById(project.getId());
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }
    }

    /**gets a project by id
     *
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to get
     * @return              the project gotten from the id
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

    /**deletes a project by id
     *
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to delete
     * @return  result      a map containing the return message
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Map deleteProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                 @PathVariable(value = "id") long id) {
        L.d("Called");

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

    /**updates a project by id
     *
     * @param session      the session of the user calling the method as String
     * @param id           the id of the project to update
     * @return             the updated project
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


    /**gets the users of a project by id
     *
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to return the developers for
     * @return              a set of users which are developing said project
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
     *  adds a user to the project as developer
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to add a developer for
     * @param uid           the id of the user to add to the project
     * @return  result      a map containing the return message
     */
    @RequestMapping(value = "{id}/users/{uid}", method = RequestMethod.PUT)
    public Map addDeveloperToProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                         @PathVariable(value = "id") long id,
                                         @PathVariable(value = "uid") long uid) {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        //check if session is valid
        User caller = getUserFromSession( session );

        //add user to project
        try {
            UserDao dao = new UserDao();
            int updated = dao.saveRelationship(dao.queryById(uid), new ProjectDao().queryById(id));
            result.put("message", String.format("succesfully updated %d user(s)", updated));
            return result;
        } catch (ConnectException e) {
            L.e(e, "database is offline!");
            throw new DatabaseOfflineException();
        }
    }

    /**
     *  updates aa projects' settings
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to update the settings for
     * @return result      a map containing the return message
     */
    @RequestMapping(value = "{id}/settings", method = RequestMethod.PUT)
    public Map updateProjectSettings(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                     @PathVariable(value = "id") long id,
                                     @RequestBody Settings settingsWithUpdateFields) {
        L.d("Called");

        java.util.Map<String, String> result = new java.util.HashMap<>();

        if(settingsWithUpdateFields == null) {
            L.w("Update settings received with empty body");
            throw new BadRequestException("No body was passed with the request");
        }

        //check if session is valid
        User caller = getUserFromSession( session );

        Settings settings = null;
        try {
            settings = new SettingsDao().queryByProject(id);

            //update settings fields
            if(settingsWithUpdateFields.getIssuesPerCommitThreshold() != 0)
                settings.setIssuesPerCommitThreshold(settingsWithUpdateFields.getIssuesPerCommitThreshold());

            if(settingsWithUpdateFields.getProject() != null)
                settings.setProject(settingsWithUpdateFields.getProject());

            if(settingsWithUpdateFields.getStartScore() != 0)
                settings.setStartScore(settingsWithUpdateFields.getStartScore());

            settings.setNegativeScores(settingsWithUpdateFields.isNegativeScores());

            settings.setPointStealing(settingsWithUpdateFields.isPointStealing());

        } catch (ConnectException e) {
            L.e("database is offline");
            throw new DatabaseOfflineException();
        }

        //update in db
        try {
            int updated = new SettingsDao().update(settings);
            if(updated != 1) throw new KnownInternalServerError("update settings failed. updated rows = %d", updated);
            result.put("Message: ", "updating settings was successful");
            return result;
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        }

    }

    /**
     *  gets the issues for a project
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to get the issues for
     * @return              a set of issues of the project
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
     * gets the duplications for a project
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to get the duplications for
     * @return              a set of duplications of the project
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
     *gets the pushes for a project
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to get the pushes for
     * @return              a set of pushes of the project
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
     *gets the commits for a project
     * @param @param session        the session of the user calling the method as String
     * @param id                    the id of the project to get the commits for
     * @return                      a set of commits of the project
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
     * gets the businesses for a project
     * @param session       the session of the user calling the method as String
     * @param id            the id of the project to get the businesses for
     * @return              a set of businesses of the project     */
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

    /**
     * gets the gcm tokens of the users developing a project
     * @param uid           the user id to get the tokens from
     * @param projectId     the project id to get the tokens from
     * @return              a list of gcm tokens from the users of that project
     */
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
