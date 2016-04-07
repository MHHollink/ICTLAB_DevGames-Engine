package nl.devgames.controllers;

import nl.devgames.Application;
import nl.devgames.DevGamesTests;
import nl.devgames.model.User;
import nl.devgames.rest.controller.AuthController;
import nl.devgames.rest.controller.UserController;
import nl.devgames.rest.errors.InvalidSessionException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class UserControllerTest extends DevGamesTests{

    private static UserController controller = new UserController();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetOwnUserFromSessionToken() throws Exception {
        String username = "Marcel", password = "admin";

        String sessionToken = new AuthController().login(username, password).get(Application.SESSION_HEADER_KEY);
        User user = controller.getOwnUser(sessionToken);

        assertEquals(user.getUsername(), username);
    }

    @Test(expected = InvalidSessionException.class)
    public void testExceptionFromGetOwnUserWithWrongSession() throws Exception {
        String sessionToken = "FaultySessionToken";

        User user = controller.getOwnUser(sessionToken);

        assertNull(user);
    }

    @Test
    public void testUpdateOwnUserWithSessionToken() throws Exception {
        String username = "Marcel", password = "admin";

        int age;

        String sessionToken = new AuthController().login(username, password).get(Application.SESSION_HEADER_KEY);
        User user = controller.getOwnUser(sessionToken);

        age = user.getAge();

        user.setAge( age + 25 );

        controller.updateOwnUser(
                sessionToken,
                user.getId(),
                user
        );

        User updatedUser = controller.getOwnUser(sessionToken);

        assertThat(updatedUser.getAge(), not(age));
        assertThat(updatedUser.getAge(), equalTo(age+25));
    }
}
