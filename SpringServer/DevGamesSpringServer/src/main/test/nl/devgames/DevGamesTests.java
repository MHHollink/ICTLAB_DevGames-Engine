package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Marcel on 03-4-2016.
 */
public abstract class DevGamesTests {

    public Neo4JRestService dbService;

    @Before
    public void setUp() throws Exception {
        // setUpDb();
    }

    @After
    public void tearDown() throws Exception {


    }

    private void setUpDb() {
        Project[] projects = {
                new Project("Clarity","AR app for the Port of Rotterdam."),
                new Project("Adventure Track", "Geolocation based Rol playing game."),
                new Project("DevGames","Programming gamificated to ensure you code better")
        };

        UserWithPassword[] users = {
                new UserWithPassword("Marcel","Mjollnir94","Marcel",null,"Hollink",22,"App Developer", null, null ,null, null, "admin"),
                new UserWithPassword("Evestar","Evestar01","Evert-Jan",null,"Heilema",22,"Backend developer", null, null, null, null, "admin"),
                new UserWithPassword("Joris","Jorikito","Jorik",null,"Schouten",22,"Backend developer", null, null, null, null, "admin"),
        };

        Business[] businesses = {
                new Business("DevGames", new HashSet<User>(Arrays.asList(users)){}, new HashSet<>(Arrays.asList(projects)))
        };

        Commit[] commits = {
                new Commit("b699883e3ccf7afbed8573d5c8add56e12f8393e", "Added .gitattributes & .gitignore files", 1455217086),
                new Commit("58c38eb08dce96f734644a0aa17c8ff8939b531e", "Fixed SDK versions in Android gradle", 1455994686)
        };

        Push[] pushes = {
                new Push(projects[2], new HashSet<>(Arrays.asList(commits)), new HashSet<>(), new HashSet<>(), 1455994686)
        };

        Duplication[] duplications = {
                new Duplication(new HashSet<DuplicationFile>(){{
                    add(new DuplicationFile("filename", 10,17,7));
                    add(new DuplicationFile("filename", 10,17,7));
                    add(new DuplicationFile("filename", 10,17,7));
                }})
        };

        Issue[] issues = {
                new Issue("MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null, "This application is still shit", 840, 1455217086, 1459624317, 0)
        };

        dbService = Neo4JRestService.getInstance();

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
    }

}
