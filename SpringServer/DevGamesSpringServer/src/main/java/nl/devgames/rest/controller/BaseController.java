package nl.devgames.rest.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.model.UserWithPassword;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * BaseController contains every method that all other controllers should have access to.
 */
@RestController
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
    @Deprecated
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







    @RequestMapping(value = "/test/insert", method = RequestMethod.POST)
    public void setUpDb() throws ConnectException {
        Project[] projects = {new Project("Clarity","AR app for the Port of Rotterdam."), new Project("Adventure Track", "Geolocation based Rol playing game."),
                new Project("DevGames","Programming gamificated to ensure you code better")};

        UserWithPassword[] users = {
                new UserWithPassword("Marcel","Mjollnir94","Marcel",null,"Hollink",22,"App Developer", null, null ,null, null, "admin"),
                new UserWithPassword("Evestar","Evestar01","Evert-Jan",null,"Heilema",22,"Backend developer", null, null, null, null, "admin"),
                new UserWithPassword("Joris","Jorikito","Jorik",null,"Schouten",22,"Backend developer", null, null, null, null, "admin"),
        };

        Business[] businesses = {new Business("DevGames", new HashSet<User>(Arrays.asList(users)){}, new HashSet<>(Arrays.asList(projects)))};

        Commit[] commits = {
                new Commit("b699883e3ccf7afbed8573d5c8add56e12f8393e", "Added .gitattributes & .gitignore files", 1455217086),
                new Commit("58c38eb08dce96f734644a0aa17c8ff8939b531e", "Fixed SDK versions in Android gradle", 1455994686)
        };

        Push[] pushes = {new Push(projects[2], new HashSet<>(Arrays.asList(commits)), new HashSet<>(), new HashSet<>(), 1455994686)};

        Duplication[] duplications = {
                new Duplication(new HashSet<DuplicationFile>(){{add(new DuplicationFile("filename", 10,17,7));add(new DuplicationFile("filename", 10,17,7));
                    add(new DuplicationFile("filename", 10,17,7));}})
        };

        Issue[] issues = { new Issue(213456, "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null, "This application is still shit", 840, 1455217086, 1459624317, 0)};


        Neo4JRestService dbService = Neo4JRestService.getInstance();
        dbService.postQuery("MATCH n DETACH DELETE n");

        for (Push push : pushes)
            dbService.postQuery(
                    "CREATE (n:Push { timestamp: '%s' })", push.getTimestamp());

        for (Commit commit : commits)
            dbService.postQuery(
                    "CREATE (n:Commit { commitId: '%s', commitMsg: '%s', timestamp: %d })",
                    commit.getCommitId(),commit.getCommitMsg(), commit.getTimeStamp());

        for (Issue issue : issues)
            dbService.postQuery(
                    "CREATE (n:Issue { severity: '%s', component: '%s', message: '%s', status: '%s', resolution: '%s', dept: %d, startLine: %d, endLine: %d, creationDate : %d, updateData: %d, closeData: %d })",
                    issue.getSeverity(), issue.getComponent(), issue.getMessage(), issue.getStatus(), issue.getResolution(), issue.getDebt(), issue.getStartLine(), issue.getEndLine(), issue.getCreationDate(), issue.getUpdateDate(), issue.getCloseDate());

        for (Duplication duplication : duplications)
            dbService.postQuery("CREATE (n:Duplication)");

        for (Business business : businesses)
            dbService.postQuery(
                    "CREATE (n:Business { name: '%s' })", business.getName());

        for (Project project : projects)
            dbService.postQuery(
                    "CREATE (n:Project { name: '%s', description: '%s' })", project.getName(), project.getDescription());

        for (UserWithPassword user : users)
            dbService.postQuery(
                    "CREATE (n:User { username: '%s', gitUsername: '%s', firstName: '%s', lastName: '%s', age: %d, mainJob: '%s', password: '%s', gcmRegId: '%s' }) ",
                    user.getUsername(), user.getGitUsername(), user.getFirstName(), user.getLastName(), user.getAge(), user.getMainJob(), user.getPassword(), user.getGcmId());

        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Adventure Track' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'Adventure Track' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Joris' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Joris' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Marcel' }) CREATE (a)-[:has_employee]->(b)");
        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Evestar' }) CREATE (a)-[:has_employee]->(b)");
        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Joris' }) CREATE (a)-[:has_employee]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Push { timestamp: '1455994686' }) CREATE (a)-[:pushed]->(b)");
        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Commit { commitId: 'b699883e3ccf7afbed8573d5c8add56e12f8393e' }) CREATE (a)-[:contains_commit]->(b)");
        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Commit { commitId: '58c38eb08dce96f734644a0aa17c8ff8939b531e' }) CREATE (a)-[:contains_commit]->(b)");
        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Issue { creationDate: 1455217086 }) CREATE (a)-[:has_issue]->(b)");
        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Duplication) CREATE (a)-[:has_duplication]->(b)");
        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:has_project]->(b)");
        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:pushed_to]->(b)");
        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'DevGames' }) CREATE (b)-[:is_lead_by]->(a)");
        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Clarity' }) CREATE (b)-[:is_lead_by]->(a)");
        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Adventure Track' }) CREATE (b)-[:is_lead_by]->(a)");
    }
}
