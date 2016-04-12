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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

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

        String sessionToken = new AuthController().login(username, password).get(Application.SESSION_HEADER_KEY);
        User user = controller.getOwnUser(sessionToken);

        int age = user.getAge();
        String gcmId = user.getGcmId();
        String git = user.getGitUsername();
        String main = user.getMainJob();

        user.setAge( age + 25 );
        user.setGcmId( "gmc" );
        user.setGitUsername( "git");
        user.setMainJob( "job" );

        controller.updateOwnUser(
                sessionToken,
                user.getId(),
                user
        );

        User updatedUser = controller.getOwnUser(sessionToken);

        assertThat(updatedUser.getAge(), not(age));
        assertThat(updatedUser.getAge(), equalTo(age+25));

        assertThat(updatedUser.getGcmId(), not(gcmId));
        assertThat(updatedUser.getGcmId(), equalTo("gcm"));

        assertThat(updatedUser.getGitUsername(), not(git));
        assertThat(updatedUser.getGitUsername(), equalTo("git"));

        assertThat(updatedUser.getMainJob(), not(main));
        assertThat(updatedUser.getMainJob(), equalTo("job"));
    }

    @Test
    public void testUserUpdateNullFieldUnchanged() throws Exception {
        String username = "Marcel", password = "admin";

        String sessionToken = new AuthController().login(username, password).get(Application.SESSION_HEADER_KEY);
        User user = controller.getOwnUser(sessionToken);

        String first = user.getGitUsername();

        user.setGitUsername(null);

        assertThat(user.getGitUsername(), nullValue());
        assertThat(first, notNullValue());

        controller.updateOwnUser(
                sessionToken,
                user.getId(),
                user
        );

        User updatedUser = controller.getOwnUser(sessionToken);

        assertThat(first, equalTo(updatedUser.getGitUsername()));
    }
}
