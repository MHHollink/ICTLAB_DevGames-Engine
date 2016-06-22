package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import nl.devgames.model.Commit;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class CommitDaoTest extends BaseTest {

    Commit commit1 = new Commit("123456", "sdsdfsdfsf", System.currentTimeMillis());

    CommitDao dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new CommitDao();

        commit1 = dao.createIfNotExists(commit1);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                dao.queryById(
                        commit1.getId()
                ),
                equalTo(
                        commit1
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
                        "commitId",
                        commit1.getCommitId()
                ).get(0),
                equalTo(
                        commit1
                )
        );
    }

    @Test
    public void testQueryByFields() throws Exception {
        Map<String,Object> fields = new HashMap<>();

        fields.put("commitId", commit1.getCommitId());
        fields.put("commitMsg", commit1.getCommitMsg());
        fields.put("timestamp", commit1.getTimeStamp());

        assertThat(
                dao.queryByFields(
                        fields
                ).get(0),
                equalTo(
                        commit1
                )
        );
    }

    @Test
    public void testQueryForSameId() throws Exception {
        assertThat(
                dao.queryBySameId(
                        commit1
                ),
                equalTo(
                        commit1
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
        List<Commit> commits = new ArrayList<>();
        commits.add(dao.createIfNotExists(
                        new Commit("1234567", "asdasd", System.currentTimeMillis())
                )
        );
        for(Commit commit : commits) {
            pushDao.saveRelationship(push, commit);
        }

        assertThat(
                dao.queryFromProject(
                        project.getId()
                ),
                equalTo(
                        commits
                )
        );
    }

    @Test
    public void testGetCommitsFromPush() throws Exception {
        PushDao pushDao = new PushDao();
        Push push = pushDao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );
        List<Commit> commits = new ArrayList<>();
        commits.add(dao.createIfNotExists(
                        new Commit("1234567", "asdasd", System.currentTimeMillis())
                )
        );
        for(Commit commit : commits) {
            pushDao.saveRelationship(push, commit);
        }

        assertThat(
                dao.getCommitsFromPush(
                        push.getId()
                ),
                equalTo(
                        commits
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(
                dao.create(commit1)
                ,equalTo(1)
        );
    }

    @Test
    public void testCreateIfNotExists() throws Exception {
        assertThat(
                dao.createIfNotExists(
                        commit1
                ),
                equalTo(
                        commit1
                )
        );
    }

    @Test
    public void testUpdate() throws Exception {
        commit1.setCommitMsg("someMsg");

        assertThat(
                dao.update(
                        commit1
                ),
                equalTo(
                        1
                )
        );

        assertThat(
                dao.queryByField(
                        "commitMsg",
                        commit1.getCommitMsg()
                ).get(0).getCommitMsg(),
                equalTo(
                        "someMsg"
                )
        );
    }

    @Test
    public void testDelete() throws Exception {
        Commit commit = dao.createIfNotExists(
                new Commit("1234567", "asdasd", System.currentTimeMillis())
        );

        assertThat(
                dao.delete(
                        commit
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteById() throws Exception {
        Commit commit = dao.createIfNotExists(
                new Commit("1234567", "asdasd", System.currentTimeMillis())
        );

        assertThat(
                dao.deleteById(
                        commit.getId()
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteCollection() throws Exception {
        List<Commit> commits = new ArrayList<>();
        commits.add(
                dao.createIfNotExists(new Commit("1234567a", "asdasd", System.currentTimeMillis()))
        );
        commits.add(
                dao.createIfNotExists(new Commit("1234567b", "asdasd", System.currentTimeMillis()))
        );
        commits.add(
                dao.createIfNotExists(new Commit("1234567c", "asdasd", System.currentTimeMillis()))
        );

        assertThat(
                dao.delete(
                        commits
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
                dao.createIfNotExists(new Commit("1234567a", "asdasd", System.currentTimeMillis())).getId()
        );
        ids.add(
                dao.createIfNotExists(new Commit("1234567a", "asdasd", System.currentTimeMillis())).getId()
        );
        ids.add(
                dao.createIfNotExists(new Commit("1234567a", "asdasd", System.currentTimeMillis())).getId()
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