package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import nl.devgames.model.Commit;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class IssueDaoTest extends BaseTest{

    IssueDao dao;
    Issue issue1 = new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new IssueDao();
        issue1 = dao.createIfNotExists(issue1);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                dao.queryById(
                        issue1.getId()
                ),
                equalTo(
                        issue1
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
                        issue1.getKey()
                ).get(0),
                equalTo(
                        issue1
                )
        );
    }

    @Test
    public void testQueryByFields() throws Exception {
        Map<String,Object> fields = new HashMap<>();

        fields.put("key", issue1.getKey());
        fields.put("component", issue1.getComponent());
        fields.put("message", issue1.getMessage());

        assertThat(
                dao.queryByFields(
                        fields
                ).get(0),
                equalTo(
                        issue1
                )
        );
    }

    @Test
    public void testQueryForSameId() throws Exception {
        assertThat(
                dao.queryBySameId(
                        issue1
                ),
                equalTo(
                        issue1
                )
        );
    }

    @Test
    public void testQueryFromProject() throws Exception {
        Project project = new ProjectDao().createIfNotExists(
                new Project("name", "description")
        );
        PushDao pushDao = new PushDao();
        Push push = pushDao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );
        pushDao.saveRelationship(push, project);
        List<Issue> issues = new ArrayList<>();
        issues.add(dao.createIfNotExists(
                    new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)
                )
        );
        for(Issue issue : issues) {
            pushDao.saveRelationship(push, issue);
        }

        assertThat(
                dao.queryFromProject(
                        project.getId()
                ),
                equalTo(
                        issues
                )
        );
    }

    @Test
    public void testGetIssuesFromPush() throws Exception {
        PushDao pushDao = new PushDao();
        Push push = pushDao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );
        List<Issue> issues = new ArrayList<>();
        issues.add(dao.createIfNotExists(
                        new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)
                )
        );
        for(Issue issue : issues) {
            pushDao.saveRelationship(push, issue);
        }

        assertThat(
                dao.getIssuesFromPush(
                        push.getId()
                ),
                equalTo(
                        issues
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(
                dao.create(issue1)
                ,equalTo(1)
        );
    }

    @Test
    public void testCreateIfNotExists() throws Exception {
        assertThat(
                dao.createIfNotExists(
                        issue1
                ),
                equalTo(
                        issue1
                )
        );
    }

    @Test
    public void testUpdate() throws Exception {
        issue1.setMessage("someMsg");

        assertThat(
                dao.update(
                        issue1
                ),
                equalTo(
                        1
                )
        );

        assertThat(
                dao.queryByField(
                        "message",
                        issue1.getMessage()
                ).get(0).getMessage(),
                equalTo(
                        "someMsg"
                )
        );
    }

    @Test
    public void testDelete() throws Exception {
        Issue issue = dao.createIfNotExists(
                new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)
        );

        assertThat(
                dao.delete(
                        issue
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteById() throws Exception {
        Issue issue = dao.createIfNotExists(
                new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)
        );

        assertThat(
                dao.deleteById(
                        issue.getId()
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteCollection() throws Exception {
        List<Issue> issues = new ArrayList<>();
        issues.add(
                dao.createIfNotExists(new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L))
        );
        issues.add(
                dao.createIfNotExists(new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L))
        );
        issues.add(
                dao.createIfNotExists(new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L))
        );

        assertThat(
                dao.delete(
                        issues
                ),
                equalTo(
                        3
                )
        );
    }

    @Test
    public void testDeleteIds() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(
                dao.createIfNotExists(new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)).getId()
        );
        ids.add(
                dao.createIfNotExists(new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)).getId()
        );
        ids.add(
                dao.createIfNotExists(new Issue(UUID.randomUUID().toString(), "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,"Define a constant instead of duplicating this literal [New Text] 8 times.", 840, 1455217086L, 1459624317L, 0L)).getId()
        );

        assertThat(
                dao.deleteIds(
                        ids
                ),
                equalTo(
                        3
                )
        );
    }
}