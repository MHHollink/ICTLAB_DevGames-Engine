package nl.devgames.rest.controller;

import nl.devgames.BaseTest;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Marcel on 17-5-2016.
 */
public class AuthControllerTest extends BaseTest {

    AuthController controller = new AuthController();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testLogin() throws Exception {
        new UserDao().createIfNotExists(new User("testUsername", null, null, null, null, "testPassword"));
        controller.login("testUsername", "testPassword");
    }
}