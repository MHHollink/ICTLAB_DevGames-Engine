package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import nl.devgames.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class UserDaoTest extends BaseTest {
    User loggedInUser = new User("TestUser", "TestGitUser", "TestFName", "TestTween","TestLName", 25, "TestJob", null, null, null, null, "TestPassword");
    UserDao dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new UserDao();

        loggedInUser = dao.createIfNotExists(loggedInUser);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                dao.queryById(
                        loggedInUser.getId()
                ),
                equalTo(
                        loggedInUser
                )
        );
    }

    @Test
    public void testQueryForAll() throws Exception {
        assertThat(
                dao.queryForAll(),
                hasSize(1)
        );
    }

    @Test
    public void testQueryByField() throws Exception {
        assertThat(
                dao.queryByField(
                        "gitUsername",
                        loggedInUser.getGitUsername()
                ).get(0),
                equalTo(
                        loggedInUser
                )
        );
    }

    @Test
    public void testQueryByFields() throws Exception {
        Map<String,Object> fields = new HashMap<>();

        fields.put("username", loggedInUser.getUsername());
        fields.put("password", loggedInUser.getPassword());
        fields.put("age", loggedInUser.getAge());

        assertThat(
                dao.queryByFields(
                        fields
                ).get(0),
                equalTo(
                        loggedInUser
                )
        );
    }

    @Test
    public void testQueryForSameId() throws Exception {
        assertThat(
                dao.queryBySameId(
                        loggedInUser
                ),
                equalTo(
                        loggedInUser
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(
                dao.create(loggedInUser)
                ,equalTo(1)
        );
    }

    @Test
    public void testCreateIfNotExists() throws Exception {
        assertThat(
                dao.createIfNotExists(
                        loggedInUser
                ),
                equalTo(
                        loggedInUser
                )
        );
    }

    @Test
    public void testUpdate() throws Exception {

        loggedInUser.setSessionId("someSession");

        assertThat(
                dao.update(
                        loggedInUser
                ),
                equalTo(
                        1
                )
        );

        assertThat(
                dao.queryBySameId(
                        loggedInUser
                ).getSessionId(),
                equalTo(
                        "someSession"
                )
        );
    }

    @Test
    public void testDeleteByUser() throws Exception {

        User user = dao.createIfNotExists(
                new User("henk", "Mans", "henkie", null, "Mans", "w8woord")
        );

        assertThat(
                dao.delete(
                        user
                ),
                equalTo(
                        1
                )
        );

    }

    @Test
    public void testDeleteById() throws Exception {
        User user = dao.createIfNotExists(
                new User("henk", "Mans", "henkie", null, "Mans", "w8woord")
        );

        assertThat(
                dao.deleteById(
                        user.getId()
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteByUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(
                dao.createIfNotExists(new User("henk", "Mans", "henkie", null, "Mans", "w8woord"))
        );
        users.add(
                dao.createIfNotExists(new User("henk2", "Mans", "henkie", null, "Mans", "w8woord"))
        );
        users.add(
                dao.createIfNotExists(new User("henk3", "Mans", "henkie", null, "Mans", "w8woord"))
        );

        assertThat(
                dao.delete(
                        users
                ),
                equalTo(
                        3
                )
        );
    }

    @Test
    public void testDeleteIds() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(
                dao.createIfNotExists(new User("henk", "Mans", "henkie", null, "Mans", "w8woord")).getId()
        );
        ids.add(
                dao.createIfNotExists(new User("henk2", "Mans", "henkie", null, "Mans", "w8woord")).getId()
        );
        ids.add(
                dao.createIfNotExists(new User("henk3", "Mans", "henkie", null, "Mans", "w8woord")).getId()
        );

        assertThat(
                dao.deleteIds(
                        ids
                ),
                equalTo(
                        3
                )
        );
    }
}