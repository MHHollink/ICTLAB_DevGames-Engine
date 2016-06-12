package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.*;
import nl.devgames.model.*;
import nl.devgames.utils.L;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class BaseTest {

    public Project project1 = new Project("Clarity","AR app for the Port of Rotterdam.", UUID.randomUUID().toString());
    public Settings project1Settings = new Settings();
    public User loggedInUser = new User("TestUser", "TestGitUser", "TestFName", "TestTween","TestLName", 25, "TestJob", null, null, null, null, "TestPassword");
    public Commit commit1 = new Commit("123456", "sdsdfsdfsf", System.currentTimeMillis());
    public Duplication duplication1 = new Duplication();
    public Business business1 = new Business("DevGames");
    public Push push1 = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
    public Issue issue1 = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);

    @Before
    public void setUp() throws Exception {
        Neo4JRestService dbService = Neo4JRestService.getInstance();
        dbService.postQuery("MATCH n DETACH DELETE n");

        UserDao userDao = new UserDao();
        CommitDao commitDao = new CommitDao();
        ProjectDao projectDao = new ProjectDao();
        IssueDao issueDao = new IssueDao();
        DuplicationDao duplicationDao = new DuplicationDao();
        BusinessDao businessDao = new BusinessDao();
        PushDao pushDao = new PushDao();
        SettingsDao settingsDao = new SettingsDao();

        userDao.create(loggedInUser);
        loggedInUser = userDao.queryByField("username", "TestUser").get(0);

        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        duplication1.setFiles(files);
        duplication1 = duplicationDao.createIfNotExists(duplication1);

        project1 = projectDao.createIfNotExists(project1);
        project1Settings.setDefault();
        project1Settings = new SettingsDao().createIfNotExists(project1Settings); // Set default and create settings
        projectDao.saveRelationship(project1, project1Settings); // Set settings as project settings

        userDao.saveRelationship(loggedInUser, project1);

        business1 = businessDao.createIfNotExists(business1);
        businessDao.saveRelationship(business1, loggedInUser);

        commit1 = commitDao.createIfNotExists(commit1);
        issue1 = issueDao.createIfNotExists(issue1);
        push1 = pushDao.createIfNotExists(push1);

        pushDao.saveRelationship(push1, commit1);
        pushDao.saveRelationship(push1, project1);
        pushDao.saveRelationship(push1, duplication1);
        pushDao.saveRelationship(push1, issue1);
        userDao.saveRelationship(loggedInUser, push1);

    }

    @After
    public void tearDown() throws Exception {
        Neo4JRestService.getInstance().postQuery("MATCH n DETACH DELETE n");
    }

    @Test
    public void testName() throws Exception {
//
//        List<Runnable> runnables = Collections.nCopies(100000, () -> {
//            L.i("Logging from runnable");
//        });
//
//        runnables.parallelStream().forEach(Runnable::run);
    }
}
