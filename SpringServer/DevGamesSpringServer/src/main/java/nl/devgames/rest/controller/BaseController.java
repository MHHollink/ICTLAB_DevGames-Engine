package nl.devgames.rest.controller;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.BusinessDao;
import nl.devgames.connection.database.dao.CommitDao;
import nl.devgames.connection.database.dao.DuplicationDao;
import nl.devgames.connection.database.dao.IssueDao;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * BaseController contains every method that all other controllers should have access to.
 */
@RestController
public abstract class BaseController {

    protected User getUserFromSession(String session) {
        if (session == null || session.isEmpty())
            throw new InvalidSessionException("Request without session"); // throws exception when session is null or blank
        try {
            return new UserDao().queryByField("session", session).get(0);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new DatabaseOfflineException();
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }

    @RequestMapping(value = "/test/insert", method = RequestMethod.POST)
    public boolean setUpDb() throws ConnectException {

        // A list of all projects that are used for demo
        Project clarity = new Project("Clarity","AR app for the Port of Rotterdam.", UUID.randomUUID().toString());
        Project adventure = new Project("Adventure Track", "Geolocation based Rol playing game.");
        Project devGamesE = new Project("DevGames-Engine","RuleServer that calculates score from Plugin and Cominucates with Web/App", UUID.randomUUID().toString());
        Project devGamesP = new Project("DevGames-Plugin","Jenkins plugin for DevGames Project");
        Project devGamesW = new Project("DevGames-Web","Webapplication for users to configure their projects and accounts in DevGames");
        Project devGamesA = new Project("DevGames-App","App that belongs to DevGames Project");

        // Users that are used in the app for demo
        User marcel = new User("Marcel","Mjollnir94","Marcel",null,"Hollink", "DevGames");
        User evert = new User("Evestar","Evestar01","Evert-Jan",null,"Heilema", "admin");
        User jorik = new User("Joris","Jorikito","Jorik",null,"Schouten", "admin");

        Business devGames = new Business("DevGames");

        // Commits by Marcel
        Commit devGamesE_commitA = new Commit("033e838638b180fd97592d127b9e8545ff75e002", "Dao get user with projects and pushes. Added check if DTOs are equal in content", 1455217086);
        Commit devGamesE_commitB = new Commit("287c4aa06612be94a9ae600b28a150afc43218b6", "full filled my job to implement UserDAO", 1455217086);
        Commit devGamesE_commitC = new Commit("d04d11bf2cb13c52f3ec83e786a2e7bf06efb3b2", "index out of bounds exception, for when database query results in 0 objects", 1455217086);
        Commit devGamesE_commitD = new Commit("c0758c8adfa41f3416d42a2cb289afa23d21a764", "some minor changes", 1455217086);
        Commit devGamesE_commitE = new Commit("3da75beb21c9bc9c26bd573f9ef2b36418388ef8", "Rewrote /login call and fixed update in user dao", 1455217086);

        // Commits by Evert
        Commit devGamesP_commitF = new Commit("192480ba70e536233a241b8263ebb1e0c7898dd3", "Removed no longer necessary class and fix couple of bugs", 1455217086);
        Commit devGamesP_commitG = new Commit("6063189b1ffefcbd5cbc898f526acf0a3b8723cc", "Working (but somewhat unreliable) plugin", 1455217086);

        Commit devGamesE_commitH = new Commit("b7c56b71b166dcc6713f219f3de142c9d37535ac", "Merge branch develop into devgames_gcm", 1455217086);
        Commit devGamesE_commitI = new Commit("7fe91ccf09e92561fa3f4f6c46384d79545e292d", "push controller done, push dao essential calls done", 1455217086);

        Push devGamesE_pushAC = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
        Push devGamesE_pushD = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
        Push devGamesE_pushE = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
        Push devGamesP_pushF = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
        Push devGamesP_pushG = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
        Push devGamesE_pushHI = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));

        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        Duplication duplicationA = new Duplication(files);
        Duplication duplicationB = new Duplication(files);
        Duplication duplicationC = new Duplication(files);

        Issue issueA = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueB = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Replace this usage of System.out or System.err by a logger.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueC = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueD = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Replace this usage of System.out or System.err by a logger.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueE = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Replace this usage of System.out or System.err by a logger.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueF = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueG = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);
        Issue issueH = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"This application is still shit", 840, 1455217086L, 1459624317L, 0L);

        Neo4JRestService dbService = Neo4JRestService.getInstance();
        dbService.postQuery("MATCH n DETACH DELETE n");

        UserDao userDao = new UserDao();
        CommitDao commitDao = new CommitDao();
        ProjectDao projectDao = new ProjectDao();
        IssueDao issueDao = new IssueDao();
        DuplicationDao duplicationDao = new DuplicationDao();
        BusinessDao businessDao = new BusinessDao();
        PushDao pushDao = new PushDao();

        // INSERTING!
        marcel = userDao.createIfNotExists(marcel);
        evert = userDao.createIfNotExists(evert);
        jorik = userDao.createIfNotExists(jorik);

        adventure = projectDao.createIfNotExists(adventure);
        clarity   = projectDao.createIfNotExists(clarity);
        devGamesA = projectDao.createIfNotExists(devGamesA);
        devGamesP = projectDao.createIfNotExists(devGamesP);
        devGamesE = projectDao.createIfNotExists(devGamesE);
        devGamesW = projectDao.createIfNotExists(devGamesW);

        userDao.saveRelationship(marcel, adventure);
        userDao.saveRelationship(marcel, clarity);
        userDao.saveRelationship(marcel, devGamesA);
        userDao.saveRelationship(marcel, devGamesE);
        userDao.saveRelationship(jorik, clarity);
        userDao.saveRelationship(jorik, devGamesA);
        userDao.saveRelationship(jorik, devGamesE);
        userDao.saveRelationship(evert, adventure);
        userDao.saveRelationship(evert, clarity);
        userDao.saveRelationship(evert, devGamesP);
        userDao.saveRelationship(evert, devGamesW);

        devGames = businessDao.createIfNotExists(devGames);
        businessDao.saveRelationship(devGames, marcel);
        businessDao.saveRelationship(devGames, evert);
        businessDao.saveRelationship(devGames, jorik);
        businessDao.saveRelationship(devGames, devGamesA);
        businessDao.saveRelationship(devGames, devGamesE);
        businessDao.saveRelationship(devGames, devGamesW);
        businessDao.saveRelationship(devGames, devGamesP);


        // Pushes By user : Marcel
        devGamesE_commitA = commitDao.createIfNotExists(devGamesE_commitA);
        devGamesE_commitB = commitDao.createIfNotExists(devGamesE_commitB);
        devGamesE_commitC = commitDao.createIfNotExists(devGamesE_commitC);
        devGamesE_pushAC  = pushDao.createIfNotExists(devGamesE_pushAC);
        issueA = issueDao.createIfNotExists(issueA);
        issueB = issueDao.createIfNotExists(issueB);
        issueC = issueDao.createIfNotExists(issueC);
        issueD = issueDao.createIfNotExists(issueD);
        duplicationA = duplicationDao.createIfNotExists(duplicationA);
        duplicationB = duplicationDao.createIfNotExists(duplicationB);
        pushDao.saveRelationship(devGamesE_pushAC, devGamesE_commitA);
        pushDao.saveRelationship(devGamesE_pushAC, devGamesE_commitB);
        pushDao.saveRelationship(devGamesE_pushAC, devGamesE_commitC);
        pushDao.saveRelationship(devGamesE_pushAC, devGamesE);
        pushDao.saveRelationship(devGamesE_pushAC, duplicationA);
        pushDao.saveRelationship(devGamesE_pushAC, duplicationB);
        pushDao.saveRelationship(devGamesE_pushAC, issueA);
        pushDao.saveRelationship(devGamesE_pushAC, issueB);
        pushDao.saveRelationship(devGamesE_pushAC, issueC);
        pushDao.saveRelationship(devGamesE_pushAC, issueD);
        userDao.saveRelationship(marcel, devGamesE_pushAC);

        devGamesE_commitD = commitDao.createIfNotExists(devGamesE_commitD);
        devGamesE_pushD = pushDao.createIfNotExists(devGamesE_pushD);
        pushDao.saveRelationship(devGamesE_pushD, devGamesE_commitD);
        userDao.saveRelationship(marcel, devGamesE_pushD);
        pushDao.saveRelationship(devGamesE_pushD, devGamesE);

        devGamesE_commitE = commitDao.createIfNotExists(devGamesE_commitE);
        devGamesE_pushE = pushDao.createIfNotExists(devGamesE_pushE);
        duplicationC = duplicationDao.createIfNotExists(duplicationC);
        issueE = issueDao.createIfNotExists(issueE);
        issueF = issueDao.createIfNotExists(issueF);
        pushDao.saveRelationship(devGamesE_pushE, devGamesE_commitE);
        pushDao.saveRelationship(devGamesE_pushE, devGamesE);
        pushDao.saveRelationship(devGamesE_pushE, duplicationC);
        pushDao.saveRelationship(devGamesE_pushE, issueE);
        pushDao.saveRelationship(devGamesE_pushE, issueF);
        userDao.saveRelationship(marcel, devGamesE_pushE);

        // Pushes By user : Evert
        devGamesP_commitF = commitDao.createIfNotExists(devGamesP_commitF);
        devGamesP_pushF = pushDao.createIfNotExists(devGamesP_pushF);
        pushDao.saveRelationship(devGamesP_pushF, devGamesP_commitF);
        pushDao.saveRelationship(devGamesP_pushF, devGamesP);
        userDao.saveRelationship(evert, devGamesP_pushF);

        devGamesP_commitG = commitDao.createIfNotExists(devGamesP_commitG);
        devGamesP_pushG = pushDao.createIfNotExists(devGamesP_pushG);
        issueG = issueDao.createIfNotExists(issueG);
        pushDao.saveRelationship(devGamesP_pushG, devGamesP_commitG);
        pushDao.saveRelationship(devGamesP_pushG, devGamesP);
        pushDao.saveRelationship(devGamesP_pushG, issueG);
        userDao.saveRelationship(evert, devGamesP_pushG);

        // Pushes By user : Jorik
        devGamesE_commitH = commitDao.createIfNotExists(devGamesE_commitH);
        devGamesE_commitI = commitDao.createIfNotExists(devGamesE_commitI);
        devGamesE_pushHI = pushDao.createIfNotExists(devGamesE_pushHI);
        issueH = issueDao.createIfNotExists(issueH);
        pushDao.saveRelationship(devGamesE_pushHI, devGamesE_commitH);
        pushDao.saveRelationship(devGamesE_pushHI, devGamesE_commitI);
        pushDao.saveRelationship(devGamesE_pushHI, issueH);
        pushDao.saveRelationship(devGamesE_pushHI, devGamesE);
        userDao.saveRelationship(jorik, devGamesE_pushHI);

        return true;
    }

    @RequestMapping(value = "/test/db", method = RequestMethod.GET)
    public Map<String, List<Object>> dumpDB() throws ConnectException {

        Map<String, List<Object>> data = new HashMap<>();

        data.put("business", new ArrayList<>(new BusinessDao().queryForAll()));
        data.put("users", new ArrayList<>(new UserDao().queryForAll()));
        data.put("projects", new ArrayList<>(new ProjectDao().queryForAll()));
        data.put("pushes", new ArrayList<>(new PushDao().queryForAll()));
        data.put("commits", new ArrayList<>(new CommitDao().queryForAll()));
        data.put("issues", new ArrayList<>(new IssueDao().queryForAll()));
        data.put("duplication", new ArrayList<>(new DuplicationDao().queryForAll()));

        return data;
    }



}
