package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.CommitDTO;
import nl.devgames.connection.database.dto.DuplicationDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Push;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorikito on 18-May-16.
 */
public class CommitDao implements Dao<Commit, Long>  {

    @Override
    public Commit queryForId(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Commit> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Commit> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Commit> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Commit queryForSameId(Commit data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    public List<Commit> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Commit)<-[:contains_commits]-(b:Push)-[:pushed_to]->(c:Project) " +
                        "WHERE ID(c) = %d " +
                        "RETURN a",
                id
        );

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(responseString)) {
            response.add(new CommitDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public List<Commit> getCommitsFromPush(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Commit)<-[:contains_commits]-(b:Push) " +
                        "WHERE ID(b) = %d " +
                        "RETURN a",
                id
        );

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(responseString)) {
            response.add(new CommitDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Commit data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public Commit createIfNotExists(Commit data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int update(Commit data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Commit data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Commit> datas) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteIds(Collection<Long> longs) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }
}
