package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


public class UserDaoTest extends BaseTest {

    UserDao dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new UserDao();
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
        throw new Exception();
    }

    @Test
    public void testDeleteById() throws Exception {
        throw new Exception();
    }

    @Test
    public void testDeleteByUsers() throws Exception {
        throw new Exception();
    }

    @Test
    public void testDeleteIds() throws Exception {
        throw new Exception();
    }
}