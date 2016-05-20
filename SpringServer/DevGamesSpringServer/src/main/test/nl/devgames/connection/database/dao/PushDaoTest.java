package nl.devgames.connection.database.dao;

import nl.devgames.model.Push;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Marcel on 19-5-2016.
 */
public class PushDaoTest {

    Push testPush = new Push(
            "TestIssueId", System.currentTimeMillis(), 147
    );

    PushDao dao;

    @Before
    public void setUp() throws Exception {
        dao = new PushDao();

        dao.create(testPush);
        testPush = dao.queryByField("issueId", testPush.getIssueId()).get(0);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testQueryForId() throws Exception {

    }

    @Test
    public void testQueryForAll() throws Exception {

    }

    @Test
    public void testQueryByField() throws Exception {

    }

    @Test
    public void testQueryByFields() throws Exception {

    }

    @Test
    public void testQueryForSameId() throws Exception {

    }

    @Test
    public void testQueryFromProject() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {

    }

    @Test
    public void testCreateIfNotExists() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testDeleteById() throws Exception {

    }

    @Test
    public void testDelete1() throws Exception {

    }

    @Test
    public void testDeleteIds() throws Exception {

    }
}