package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.DuplicationDTO;
import nl.devgames.connection.database.dto.DuplicationFileDTO;
import nl.devgames.connection.database.dto.IssueDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.model.Issue;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.*;

/**
 * Created by Jorikito on 18-May-16.
 */
public class DuplicationDao  implements Dao<Duplication, Long>  {
    @Override
    public Duplication queryById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
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
    public Duplication queryBySameId(Duplication data) throws ConnectException, IndexOutOfBoundsException {
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

    public List<Duplication> getDuplicationsFromPush(long id) throws ConnectException {

        List<Duplication> duplicationList = new ArrayList<>();
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:DuplicationFile)<-[:has_files]-(b:Duplication)<-[:has_duplications]-(c:Push " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}," +
                        "       {id:id(b), labels: labels(b), data: b}",
                id);

        JsonObject json = new JsonParser().parse(responseString).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());

        JsonArray data = json.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        for (JsonElement element : data) {
            JsonArray rows = element.getAsJsonObject().get("row").getAsJsonArray();

            DuplicationDTO dto = null; Set<DuplicationFile> files = new HashSet<>();

            for ( JsonElement row : rows) {
                String label = row.getAsJsonObject().get("labels").getAsJsonArray().get(0).getAsString();

                switch (label) {
                    case "Duplication" :
                        if(dto == null) dto = new DuplicationDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            DuplicationDTO dTemp = new DuplicationDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(dTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, dTemp);
                        }
                        break;
                    case "DuplicationFile" :
                        files.add(new DuplicationFileDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
            if(dto == null) return null;
            dto.files = files;
            duplicationList.add(dto.toModel());
        }

        return duplicationList;
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
