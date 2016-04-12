package nl.devgames.controllers;

import nl.devgames.Application;
import nl.devgames.DevGamesTests;
import nl.devgames.model.User;
import nl.devgames.rest.controller.AuthController;
import nl.devgames.rest.controller.UserController;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class UserControllerTest extends DevGamesTests{

    private static UserController controller = new UserController();
    private static String sessionToken;
    private static String username = "Marcel", password = "admin";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        sessionToken = new AuthController().login(username, password).get(Application.SESSION_HEADER_KEY);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetOwnUserFromSessionToken() throws Exception {
        User user = controller.getOwnUser(sessionToken);
        assertEquals(user.getUsername(), username);
    }

    @Test(expected = InvalidSessionException.class)
    public void testExceptionFromGetOwnUserWithWrongSession() throws Exception {
        String sessionToken = "FaultySessionToken";
        User user = controller.getOwnUser(sessionToken);
        assertNull(user);
    }

    @Test(expected = BadRequestException.class)
    public void testGetOwnUserWithoutSessionToken() throws Exception {
        User user = controller.getOwnUser(null);
        assertNull(user);
    }

//    @Test
//    public void testUpdateOwnUserWithSessionToken() throws Exception {
//        User user = controller.getOwnUser(sessionToken);
//
//        int age = user.getAge();
//        String gcmId = user.getGcmId();
//        String git = user.getGitUsername();
//        String main = user.getMainJob();
//
//        user.setAge( age + 25 );
//        user.setGcmId( "gmc" );
//        user.setGitUsername( "git");
//        user.setMainJob( "job" );
//
//        controller.updateOwnUser(
//                sessionToken,
//                user.getId(),
//                user
//        );
//
//        User updatedUser = controller.getOwnUser(sessionToken);
//
//        assertThat(updatedUser.getAge(), not(age));
//        assertEquals(updatedUser.getAge(), age+25);
//
//        assertThat(updatedUser.getGcmId(), not(gcmId));
//        assertEquals(updatedUser.getGcmId(), "gcm");
//
//        assertThat(updatedUser.getGitUsername(), not(git));
//        assertEquals(updatedUser.getGitUsername(), "git");
//
//        assertThat(updatedUser.getMainJob(), not(main));
//        assertEquals(updatedUser.getMainJob(), "job");
//    }

//    @Test
//    public void testUserUpdateNullFieldUnchanged() throws Exception {
//        User user = controller.getOwnUser(sessionToken);
//
//        String first = user.getGitUsername();
//
//        user.setGitUsername(null);
//
//        assertNull(user.getGitUsername());
//        assertNotNull(first);
//
//        controller.updateOwnUser(
//                sessionToken,
//                user.getId(),
//                user
//        );
//
//        User updatedUser = controller.getOwnUser(sessionToken);
//
//        assertEquals(first, updatedUser.getGitUsername());
//    }


    @Test
    public void testGetOtherUserById() throws Exception {
        Long otherUserId =
                controller.getOwnUser(
                        new AuthController()
                                .login("Evestar", password)
                                .get(Application.SESSION_HEADER_KEY)
                ).getId();
        User otherUser = controller.getUser(sessionToken, otherUserId);
        assertEquals(otherUser.getId(), otherUserId);
        assertEquals(otherUser.getUsername(), "Evestar");
    }

    @Test (expected = InvalidSessionException.class)
    public void testGetOtherUserWithInvalidSession() throws Exception {
        String sessionToken = "FaultySessionToken";
        Long otherUserId =
                controller.getOwnUser(
                        new AuthController()
                                .login("Evestar", "admin")
                                .get(Application.SESSION_HEADER_KEY)
                ).getId();
        controller.getUser(sessionToken, otherUserId);
    }

    @Test (expected = NotFoundException.class)
    public void testGetOtherUserWithWrongId() throws Exception {
        User user = controller.getUser(sessionToken, -1L);
        assertNull(user);
    }

    @Test (expected = BadRequestException.class)
    public void testGetOtherUserWithoutSession() throws Exception {
        Long otherUserId =
                controller.getOwnUser(
                        new AuthController()
                                .login("Evestar", "admin")
                                .get(Application.SESSION_HEADER_KEY)
                ).getId();
        User user = controller.getUser(null, otherUserId);
        assertNull(user);
    }

    @Test (expected = BadRequestException.class)
    public void testGetOtherUserWithoutSessionWrongId() throws Exception {
        User user = controller.getUser(null, -1L);
        assertNull(user);
    }

    @Test (expected = InvalidSessionException.class)
    public void testGetOtherUserInvalidSessionWrongId() throws Exception {
        String sessionToken = "FaultySessionToken";
        User user = controller.getUser(sessionToken, -1L);
        assertNull(user);
    }











}
