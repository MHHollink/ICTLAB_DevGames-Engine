package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.DuplicationDTO;
import nl.devgames.connection.database.dto.DuplicationFileDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jorikito on 18-May-16.
 */
public class DuplicationDao extends AbsDao<Duplication, Long>  {
    @Override
    public Duplication queryById(Long id) throws ConnectException, IndexOutOfBoundsException {
        DuplicationDTO dto = null;
        Set<DuplicationFile> files = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Duplication) " +
                        "WHERE ID(a) = %d " +
                        "OPTIONAL " +
                        "MATCH a-[]->(b) " +
                        "WHERE ID(a) = %d " +
                        "RETURN " +
                        "{id:id(a), labels: labels(a), data: a}," +
                        "{id:id(b), labels: labels(b), data: b}",
                id, id
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());

        JsonArray data = json.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        for (JsonElement element : data) {
            JsonArray rows = element.getAsJsonObject().get("row").getAsJsonArray();

            for ( JsonElement row : rows) {
                JsonElement labels = row.getAsJsonObject().get("labels");
                if (labels instanceof JsonNull)
                    continue;
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
        }
        if(dto == null) return null;

        dto.files = files;

        return dto.toModel();
    }

    @Override
    public List<Duplication> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Duplication) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Duplication> response = new ArrayList<>();
        for (JsonObject object : DuplicationDTO.findAll(r)) {
            response.add(new DuplicationDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Duplication> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Duplication) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Duplication) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        List<Duplication> response = new ArrayList<>();
        for (JsonObject object : DuplicationDTO.findAll(r)) {
            response.add(
                    queryById(
                            new DuplicationDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Duplication> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat = "MATCH (n:Duplication) WHERE ";

        Iterator<String> iterator = fieldValues.keySet().iterator();
        while (iterator.hasNext()) {
            String field = iterator.next();
            Object value = fieldValues.get(field);

            if(value instanceof Number)
                queryFormat += String.format("n.%s = %s ", field, value);
            else
                queryFormat += String.format("n.%s = '%s' ", field, value);

            if (iterator.hasNext())
                queryFormat += "AND ";
        }

        queryFormat += " RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat
        );

        List<Duplication> response = new ArrayList<>();
        for (JsonObject object : DuplicationDTO.findAll(r)) {
            response.add(
                    queryById(
                            new DuplicationDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Duplication queryBySameId(Duplication duplication) throws ConnectException, IndexOutOfBoundsException {
        return queryById(duplication.getId());
    }

    public List<Duplication> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Duplication)<-[:has_duplications]-(b:Push)-[:pushed_to]->(c:Project) " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
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
    public int create(Duplication duplication) throws ConnectException, IndexOutOfBoundsException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Duplication) RETURN {id:id(n), labels: labels(n), data: n} "
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Duplication createIfNotExists(Duplication data) throws ConnectException, IndexOutOfBoundsException {
        Duplication duplication = data.getId() != null ? queryById(data.getId()) : null;
        if (duplication == null || !duplication.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            //todo: return what?
            return queryById(data.getId());
        } else return duplication;
    }

    //todo: is deze nodig marcel?
    @Override
    public int update(Duplication duplication) throws ConnectException, IndexOutOfBoundsException {
        if(duplication != null && queryById(duplication.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Duplication) " +
                            "WHERE ID(n) = %d " +
                            "RETURN {id:id(n), labels: labels(n), data: n} ",
                    duplication.getId()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Duplication is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Duplication duplication) throws ConnectException, IndexOutOfBoundsException {
        return deleteById(duplication.getId());
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Duplication> duplications) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Duplication duplication : duplications)
            changed += delete(duplication);
        return changed;
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Long id : ids)
            changed += deleteById(id);
        return changed;
    }
}
