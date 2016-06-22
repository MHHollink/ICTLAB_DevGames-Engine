package nl.devgames.connection.database.dao;

import nl.devgames.BaseTest;
import nl.devgames.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DuplicationDaoTest extends BaseTest {

    Duplication duplication1 = new Duplication();
    DuplicationDao dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new DuplicationDao();

        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        duplication1.setFiles(files);
        duplication1 = dao.createIfNotExists(duplication1);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testQueryForId() throws Exception {
        assertThat(
                dao.queryById(
                        duplication1.getId()
                ),
                equalTo(
                        duplication1
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
                        "generatedUUID",
                        duplication1.getUuid()
                ).get(0),
                equalTo(
                        duplication1
                )
        );
    }

    @Test
    public void testQueryByFields() throws Exception {
        testQueryByField(); // only has 1 field
    }

    @Test
    public void testQueryForSameId() throws Exception {
        assertThat(
                dao.queryBySameId(
                        duplication1
                ),
                equalTo(
                        duplication1
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
        List<Duplication> duplications = new ArrayList<>();
        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        Duplication duplication = new Duplication(files);
        duplications.add(dao.createIfNotExists(
                        duplication
                )
        );
        for(Duplication d : duplications) {
            pushDao.saveRelationship(push, d);
        }

        assertThat(
                dao.queryFromProject(
                        project.getId()
                ),
                equalTo(
                        duplications
                )
        );
    }

    @Test
    public void testGetDuplicationsFromPush() throws Exception {
        PushDao pushDao = new PushDao();
        Push push = pushDao.createIfNotExists(
                new Push(UUID.randomUUID().toString(), 1455994686, (new Random().nextInt(150)+100))
        );
        List<Duplication> duplications = new ArrayList<>();
        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        Duplication duplication = new Duplication(files);
        duplications.add(dao.createIfNotExists(
                        duplication
                )
        );
        for(Duplication d : duplications) {
            pushDao.saveRelationship(push, d);
        }

        assertThat(
                dao.getDuplicationsFromPush(
                        push.getId()
                ),
                equalTo(
                        duplications
                )
        );
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(
                dao.create(duplication1)
                ,equalTo(1)
        );
    }

    @Test
    public void testCreateIfNotExists() throws Exception {
        assertThat(
                dao.createIfNotExists(
                        duplication1
                ),
                equalTo(
                        duplication1
                )
        );
    }

    @Test
    public void testUpdate() throws Exception {
        duplication1.setUuid("1212");

        assertThat(
                dao.update(
                        duplication1
                ),
                equalTo(
                        1
                )
        );

        assertThat(
                dao.queryByField(
                        "generatedUUID",
                        duplication1.getUuid()
                ).get(0).getUuid(),
                equalTo(
                        "1212"
                )
        );
    }

    @Test
    public void testDelete() throws Exception {
        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name", 88, 93, 93-88 ));
        Duplication duplication = dao.createIfNotExists(
                new Duplication(files)
        );

        assertThat(
                dao.delete(
                        duplication
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteById() throws Exception {
        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name2", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name2", 88, 93, 93-88 ));
        Duplication duplication = dao.createIfNotExists(
                new Duplication(files)
        );

        assertThat(
                dao.deleteById(
                        duplication.getId()
                ),
                equalTo(
                        1
                )
        );
    }

    @Test
    public void testDeleteCollection() throws Exception {
        List<Duplication> duplications = new ArrayList<>();
        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name2", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name2", 88, 93, 93-88 ));
        duplications.add(
                dao.createIfNotExists(new Duplication(files))
        );
        duplications.add(
                dao.createIfNotExists(new Duplication(files))
        );
        duplications.add(
                dao.createIfNotExists(new Duplication(files))
        );

        assertThat(
                dao.delete(
                        duplications
                ),
                equalTo(
                        3
                )
        );
    }

    @Test
    public void testDeleteIds() throws Exception {
        List<Long> ids = new ArrayList<>();
        List<Duplication> duplications = new ArrayList<>();
        Set<DuplicationFile> files = new HashSet<>();
        files.add(new DuplicationFile("File Name2", 88, 93, 93-88 ));
        files.add(new DuplicationFile("File Name2", 88, 93, 93-88 ));
        ids.add(
                dao.createIfNotExists(new Duplication(files)).getId()
        );
        ids.add(
                dao.createIfNotExists(new Duplication(files)).getId()
        );
        ids.add(
                dao.createIfNotExists(new Duplication(files)).getId()
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