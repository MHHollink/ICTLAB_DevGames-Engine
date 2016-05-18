package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.DuplicationDTO;
import nl.devgames.connection.database.dto.IssueDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorikito on 18-May-16.
 */
public class DuplicationDao  implements Dao<Duplication, Long>  {
    @Override
    public Duplication queryForId(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Duplication> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Duplication> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Duplication> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Duplication queryForSameId(Duplication data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    public List<Duplication> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Duplication)<-[:has_duplications]-(b:Push)-[:pushed_to]->(c:Project) " +
                        "WHERE ID(c) = %d " +
                        "RETURN a",
                id
        );

        List<Duplication> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new DuplicationDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Duplication data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public Duplication createIfNotExists(Duplication data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int update(Duplication data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Duplication data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Duplication> datas) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteIds(Collection<Long> longs) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }
}
