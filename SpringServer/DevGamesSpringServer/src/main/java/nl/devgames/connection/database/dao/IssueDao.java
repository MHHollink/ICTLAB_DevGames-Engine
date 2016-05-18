package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.IssueDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Issue;
import nl.devgames.model.User;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorikito on 18-May-16.
 */
public class IssueDao implements Dao<Issue, Long> { {

}

    @Override
    public Issue queryForId(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Issue> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Issue> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Issue> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Issue queryForSameId(Issue data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    public List<Issue> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Issue)<-[:has_issues]-(b:Push)-[:pushed_to]->(c:Project) " +
                        "WHERE ID(c) = %d " +
                        "RETURN a",
                id
        );

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new IssueDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public List<Issue> getIssuesFromPush(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Issue)<-[:has_issues]-(b:Push) " +
                        "WHERE ID(b) = %d " +
                        "RETURN a",
                id
        );

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new IssueDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Issue data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public Issue createIfNotExists(Issue data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int update(Issue data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Issue data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Issue> datas) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteIds(Collection<Long> longs) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }
}
