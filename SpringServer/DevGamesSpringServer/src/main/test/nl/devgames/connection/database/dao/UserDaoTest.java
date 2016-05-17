package nl.devgames.connection.database.dao;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by Marcel on 17-5-2016.
 */
public class UserDaoTest {

    User loggedInUser = new User("TestUser", "TestGitUser", "TestFName", "TestTween","TestLName", 25, "TestJob", null, null, null, null, "TestPassword");
    UserDao userDao = new UserDao();

    @Before
    public void setUp() throws Exception {
        userDao.create(loggedInUser);
        loggedInUser = userDao.queryByField("username", "TestUser").get(0);
    }

    @After
    public void tearDown() throws Exception {
        Neo4JRestService.getInstance().postQuery("MATCH n DETACH DELETE n");
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                loggedInUser,
                equalTo(
                        userDao.queryForId(
                                loggedInUser.getId()
                        )
                )
        );
    }

    @Test
    public void testQueryForAll() throws Exception {
        assertThat(
                userDao.queryForAll(),
                hasSize(1)
        );
    }

    @Test
    public void testQueryByField() throws Exception {

    }

    @Test
    public void testQueryByFields() throws Exception {

    }

    @Test
    public void testQueryForSameId() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {
        assertThat(userDao.create(loggedInUser), equalTo(1));
    }

    @Test
    public void testCreateIfNotExists() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testDeleteById() throws Exception {

    }

    @Test
    public void testDelete1() throws Exception {

    }

    @Test
    public void testDeleteIds() throws Exception {

    }
}