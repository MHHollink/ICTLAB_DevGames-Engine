package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.model.User;
import org.junit.After;
import org.junit.Before;

/**
 * Created by Marcel on 19-5-2016.
 */
public class BaseTest {

    public User loggedInUser = new User("TestUser", "TestGitUser", "TestFName", "TestTween","TestLName", 25, "TestJob", null, null, null, null, "TestPassword");


    @Before
    public void setUp() throws Exception {
        UserDao userDao = new UserDao();

        userDao.create(loggedInUser);
        loggedInUser = userDao.queryByField("username", "TestUser").get(0);
    }

    @After
    public void tearDown() throws Exception {
        Neo4JRestService.getInstance().postQuery("MATCH n DETACH DELETE n");
    }
}
