package nl.devgames.controllers;

import nl.devgames.Application;
import nl.devgames.DevGamesTests;
import nl.devgames.rest.controller.AuthController;
import nl.devgames.rest.errors.BadRequestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class AuthControllerTest extends DevGamesTests {

    private static AuthController controller = new AuthController();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetSessionFromLogin() throws Exception {
        Map<String,String> session = controller.login("Marcel", "admin");
        assertThat(session, hasKey(Application.SESSION_HEADER_KEY));
        assertThat(session.get(Application.SESSION_HEADER_KEY), notNullValue());
    }

    @Test(expected = BadRequestException.class)
    public void testExceptionFromFailedLoginWrongCombo() throws Exception {
        Map<String,String> session = controller.login("Marcel", "Error");
        assertThat(session, nullValue());
    }

    @Test(expected = BadRequestException.class)
    public void testExceptionFromFailedLoginEmptyFields() throws Exception {
        Map<String,String> session = controller.login(null, null);
        assertThat(session, nullValue());
    }

    @Test(expected = BadRequestException.class)
    public void testExceptionFromFailedLoginEmptyFieldUsername() throws Exception {
        Map<String,String> session = controller.login("Marcel", null);
        assertThat(session, nullValue());
    }

    @Test(expected = BadRequestException.class)
    public void testExceptionFromFailedLoginEmptyFieldPassword() throws Exception {
        Map<String,String> session = controller.login(null, "admin");
        assertThat(session, nullValue());
    }
}
