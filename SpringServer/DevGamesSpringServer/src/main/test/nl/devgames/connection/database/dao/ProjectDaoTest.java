package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import nl.devgames.model.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by Marcel on 19-5-2016.
 */
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
                hasSize(1)
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
        throw new Exception();
    }

    @Test
    public void testGetProjectForPush() throws Exception {
        throw new Exception();
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
        throw new Exception();
    }

    @Test
    public void testDeleteById() throws Exception {
        throw new Exception();
    }

    @Test
    public void testDelete1() throws Exception {
        throw new Exception();
    }

    @Test
    public void testDeleteIds() throws Exception {
        throw new Exception();
    }
}