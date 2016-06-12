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

public class ProjectDaoTest extends BaseTest {

    Project testProject = new Project(
            "TestProject", "TestDescription"
    );
    ProjectDao dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new ProjectDao();

        dao.create(testProject);
        testProject = dao.queryByField("name", testProject.getName()).get(0);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                dao.queryById(
                        testProject.getId()
                ),
                equalTo(
                        testProject
                )
        );
    }

    @Test
    public void testQueryForAll() throws Exception {
        assertThat(
                dao.queryForAll(),
                hasSize(2)
        );
    }

    @Test
    public void testQueryByField() throws Exception {
        assertThat(
                dao.queryByField(
                        "name",
                        testProject.getName()
                ).get(0),
                equalTo(
                        testProject
                )
        );
    }

    @Test
    public void testQueryByFields() throws Exception {
        Map<String,Object> fields = new HashMap<>();

        fields.put("name", testProject.getName());
        fields.put("description", testProject.getDescription());

        assertThat(
                dao.queryByFields(
                        fields
                ).get(0),
                equalTo(
                        testProject
                )
        );
    }

    @Test
    public void testAddUserToProject() throws Exception {
        User user = new UserDao().createIfNotExists(
                new User("TestUser", "TestGitUser", "TestFName", "TestTween","TestLName", 25, "TestJob", null, null, null, null, "TestPassword")
        );

        assertThat(
                new ProjectDao().saveRelationship(project1, user)
                ,
                equalTo(
                        1
                )
        );


    }

    @Test
    public void testGetProjectForPush() throws Exception {
        PushDao pushDao = new PushDao();
        Push push = pushDao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );
        pushDao.saveRelationship(push, project1);


        assertThat(
                dao.getProjectByPush(
                        push.getId()
                ),
                equalTo(
                        project1
                )
        );
    }

    @Test
    public void testQueryForSameId() throws Exception {
        assertThat(
                dao.queryBySameId(
                        testProject
                ),
                equalTo(
                        testProject
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(
                dao.create(testProject)
                ,equalTo(1)
        );
    }

    @Test
    public void testCreateIfNotExists() throws Exception {
        assertThat(
                dao.createIfNotExists(
                        testProject
                ),
                equalTo(
                        testProject
                )
        );
    }

    @Test
    public void testUpdate() throws Exception {
        testProject.setName("someName");

        assertThat(
                dao.update(
                        testProject
                ),
                equalTo(
                        1
                )
        );

        assertThat(
                dao.queryBySameId(
                        testProject
                ).getName(),
                equalTo(
                        "someName"
                )
        );
    }

    @Test
    public void testDelete() throws Exception {
        Project project = dao.createIfNotExists(
                new Project(
                        "TestProject", "TestDescription"
                )
        );

        assertThat(
                dao.delete(
                        project
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteById() throws Exception {
        Project project = dao.createIfNotExists(
                new Project(
                        "TestProject", "TestDescription"
                )
        );

        assertThat(
                dao.deleteById(
                        project.getId()
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteCollection() throws Exception {
        List<Project> projects = new ArrayList<>();
        projects.add(
                dao.createIfNotExists(new Project(
                        "TestProject1", "TestDescription"
                ))
        );
        projects.add(
                dao.createIfNotExists(new Project(
                        "TestProject2", "TestDescription"
                ))
        );
        projects.add(
                dao.createIfNotExists(new Project(
                        "TestProject3", "TestDescription"
                ))
        );

        assertThat(
                dao.delete(
                        projects
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
                dao.createIfNotExists(new Project(
                        "TestProject1", "TestDescription"
                )).getId()
        );
        ids.add(
                dao.createIfNotExists(new Project(
                        "TestProject2", "TestDescription"
                )).getId()
        );
        ids.add(
                dao.createIfNotExists(new Project(
                        "TestProject3", "TestDescription"
                )).getId()
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