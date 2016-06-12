package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import nl.devgames.model.Commit;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.rest.errors.BadRequestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class PushDaoTest extends BaseTest{

    Push testPush = new Push(
            "TestIssueId", System.currentTimeMillis(), 147
    );

    PushDao dao;

    @Before
    public void setUp() throws Exception {
        dao = new PushDao();

        dao.create(testPush);
        testPush = dao.queryByField("key", testPush.getKey()).get(0);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                dao.queryById(
                        testPush.getId()
                ),
                equalTo(
                        testPush
                )
        );
    }

    @Test
    public void testQueryForAll() throws Exception {
        assertThat(
                dao.queryForAll(),
                hasSize(1)
        );
    }

    @Test
    public void testQueryByField() throws Exception {
        assertThat(
                dao.queryByField(
                        "key",
                        testPush.getKey()
                ).get(0),
                equalTo(
                        testPush
                )
        );
    }

    @Test
    public void testQueryByFields() throws Exception {
        Map<String,Object> fields = new HashMap<>();

        fields.put("key", testPush.getKey());
        fields.put("timestamp", testPush.getTimestamp());

        assertThat(
                dao.queryByFields(
                        fields
                ).get(0),
                equalTo(
                        testPush
                )
        );
    }

    @Test
    public void testQueryForSameId() throws Exception {
        assertThat(
                dao.queryBySameId(
                        testPush
                ),
                equalTo(
                        testPush
                )
        );
    }

    @Test
    public void testQueryFromProject() throws Exception {
        Project project = new ProjectDao().createIfNotExists(
                new Project("name", "description")
        );
        PushDao pushDao = new PushDao();
        List<Push> pushes = new ArrayList<>();
        pushes.add(dao.createIfNotExists(
                        testPush
                )
        );
        for(Push push : pushes) {
            pushDao.saveRelationship(push, project);
        }

        assertThat(
                dao.queryFromProject(
                        project.getId()
                ),
                equalTo(
                        pushes
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(
                dao.create(push1)
                ,equalTo(1)
        );
    }

    @Test
    public void testCreateIfNotExists() throws Exception {
        assertThat(
                dao.createIfNotExists(
                        testPush
                ),
                equalTo(
                        testPush
                )
        );
    }

    @Test
    public void testUpdate() throws Exception {
        testPush.setKey("12345");

        assertThat(
                dao.update(
                        testPush
                ),
                equalTo(
                        1
                )
        );

        assertThat(
                dao.queryByField(
                        "key",
                        testPush.getKey()
                ).get(0).getKey(),
                equalTo(
                        "12345"
                )
        );
    }

    @Test
    public void testDelete() throws Exception {
        Push push = dao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );

        assertThat(
                dao.delete(
                        push
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteById() throws Exception {
        Push push = dao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );

        assertThat(
                dao.deleteById(
                        push.getId()
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteCollection() throws Exception {
        List<Push> pushes = new ArrayList<>();
        pushes.add(
                testPush
        );

        assertThat(
                dao.delete(
                        pushes
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteIds() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(
                testPush.getId()
        );

        assertThat(
                dao.deleteIds(
                        ids
                ),
                equalTo(
                        1
                )
        );
    }
}