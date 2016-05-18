package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.DevGamesTests;
import nl.devgames.model.Issue;
import nl.devgames.model.Push;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Marcel on 17-5-2016.
 */
public class IssueControllerTest {

    String sessionId = null;
    IssueController controller;
    long issueId = 0l;  //valid issue id from neo4j

    @Before
    public void setUp() throws Exception {
        super.setUp();
        sessionId = new AuthController().login("Joris", "admin").get(Application.SESSION_HEADER_KEY);
        controller = new IssueController();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetIssueById() throws Exception {
        Issue issue = controller.getIssueById(sessionId, issueId);
//        Assert
    }

    @Test
    public void testGetPush() throws Exception {
        Push push = controller.getPush(sessionId, issueId);
    }

    @Test
    public void testGetUser() throws Exception {

    }

    @Test
    public void testGetProject() throws Exception {

    }
}