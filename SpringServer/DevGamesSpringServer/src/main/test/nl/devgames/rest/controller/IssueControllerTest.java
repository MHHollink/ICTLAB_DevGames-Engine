package nl.devgames.rest.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IssueControllerTest {

    String sessionId = null;
    IssueController controller;
    long issueId = 0l;  //valid issue id from neo4j

    @Before
    public void setUp() throws Exception {
//        sessionId = new AuthController().login("Joris", "admin").get(Application.SESSION_HEADER_KEY);
//        controller = new IssueController();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetIssueById() throws Exception {
        //Issue issue = controller.getIssueById(sessionId, issueId);
//        Assert
    }

    @Test
    public void testGetPush() throws Exception {
        //Push push = controller.getPush(sessionId, issueId);
    }

    @Test
    public void testGetUser() throws Exception {

    }

    @Test
    public void testGetProject() throws Exception {

    }
}