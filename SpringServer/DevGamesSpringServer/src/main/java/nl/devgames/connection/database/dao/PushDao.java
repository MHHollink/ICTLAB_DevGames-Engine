package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.*;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Push;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.*;

/**
 * Created by Jorikito on 18-May-16.
 */
public class PushDao implements Dao<Push, Long>  {

    @Override
    public Push queryForId(Long id) throws ConnectException, IndexOutOfBoundsException {
        PushDTO dto = null; Set<Commit> commits = new HashSet<>(); Set<Issue> issues = new HashSet<>(); Set<Duplication> duplications = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[r]->(b) " +
                        "WHERE ID(a) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}," +
                        "       {id:id(b), labels: labels(b), data: b}",
                id);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());

        JsonArray data = json.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        for (JsonElement element : data) {
            JsonArray rows = element.getAsJsonObject().get("row").getAsJsonArray();

            for ( JsonElement row : rows) {
                String label = row.getAsJsonObject().get("labels").getAsJsonArray().get(0).getAsString();

                switch (label) {
                    case "Push" :
                        if(dto == null) dto = new PushDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            PushDTO pTemp = new PushDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(pTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, pTemp);
                        }
                        break;
                    case "Commit" :
                        commits.add(new CommitDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    case "Issue" :
                        issues.add(new IssueDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    case "Duplication" :
                        duplications.add(new DuplicationDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }
        if(dto == null) return null;
        dto.commits = commits;
        dto.issues = issues;
        dto.duplications = duplications;

        return dto.toModel();
    }

    @Override
    public List<Push> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Push> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Push> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Push queryForSameId(Push data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    public List<Push> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:pushed_to]->(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN a",
                id
        );

        List<Push> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new PushDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Push data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public Push createIfNotExists(Push data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int update(Push data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Push data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Push> datas) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteIds(Collection<Long> longs) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }
}
