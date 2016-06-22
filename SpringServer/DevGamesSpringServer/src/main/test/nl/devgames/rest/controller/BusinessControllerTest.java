package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.BaseTest;
import nl.devgames.connection.database.dao.BusinessDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.model.Business;
import nl.devgames.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.BufferUnderflowException;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class BusinessControllerTest extends BaseTest {

    BusinessController controller = new BusinessController();
    User user = new User("testUsername", null, null, null, null, "testPassword");
    String session = null;
    Business business1 = new Business("business test");
    BusinessDao dao;

    @Before
    public void setUp() throws Exception {
        user = new UserDao().createIfNotExists(user);
        Collection<String> values = new AuthController().login("testUsername", "testPassword").values();
        session = values.iterator().next();

        dao = new BusinessDao();
        business1 = dao.createIfNotExists(business1);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testCreateBusiness() throws Exception {
        Business business = new Business("DevGames");
        assertThat(
                controller.createBusiness(session, business)
                ,
                notNullValue()
        );
    }


    @Test
    public void testGetBusiness() throws Exception {
        assertThat(
                controller.getBusiness(session, business1.getId())
                ,
                equalTo(business1)
        );
    }

    @Test
    public void testDeleteBusiness() throws Exception {

    }

    @Test
    public void testGetEmployees() throws Exception {

    }

    @Test
    public void testAddEmployeeToBusiness() throws Exception {

    }

    @Test
    public void testGetProjects() throws Exception {

    }

    @Test
    public void testAddProjectToBusiness() throws Exception {

    }
}