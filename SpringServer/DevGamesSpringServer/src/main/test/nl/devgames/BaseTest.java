package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.*;
import nl.devgames.model.*;
import nl.devgames.utils.L;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class BaseTest {



    public Push push1 = new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100));
    public Issue issue1 = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);

    @Before
    public void setUp() throws Exception {
        Neo4JRestService dbService = Neo4JRestService.getInstance();
        dbService.postQuery("MATCH n DETACH DELETE n");


//
///        issue1 = issueDao.createIfNotExists(issue1);
//        push1 = pushDao.createIfNotExists(push1);


    }

    @After
    public void tearDown() throws Exception {
        Neo4JRestService.getInstance().postQuery("MATCH n DETACH DELETE n");
    }

    @Test
    public void testName() throws Exception {
//
//        List<Runnable> runnables = Collections.nCopies(100000, () -> {
//            L.i("Logging from runnable");
//        });
//
//        runnables.parallelStream().forEach(Runnable::run);
    }
}
