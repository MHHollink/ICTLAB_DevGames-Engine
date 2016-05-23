package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.PushDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
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
public class PushDao extends AbsDao<Push, Long>  {

    @Override
    public Push queryById(Long id) throws ConnectException, IndexOutOfBoundsException {
        PushDTO dto = null;
        Set<Commit> commits = new HashSet<>();
        Set<Issue> issues = new HashSet<>();
        Set<Duplication> duplications = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push) " +
                        "WHERE ID(a) = %d " +
                        "OPTIONAL " +
                            "MATCH (a:Push)-[r]->(b) " +
                            "WHERE ID(a) = %d "+
                        "RETURN {id:id(a), labels: labels(a), data: a}," +
                               "{id:id(b), labels: labels(b)}",
                id, id);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());

        JsonArray data = json.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        for (JsonElement element : data) {
            JsonArray rows = element.getAsJsonObject().get("row").getAsJsonArray();

            for ( JsonElement row : rows) {
                JsonElement labels =row.getAsJsonObject().get("labels");
                if(labels instanceof JsonNull) continue;
                String label = labels.getAsJsonArray().get(0).getAsString();

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
                        Commit c= new Commit();
                        c.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                        commits.add(c);
                        break;
                    case "Issue" :
                        Issue i = new Issue();
                        i.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                        issues.add(i);
                        break;
                    case "Duplication" :
                        Duplication d = new Duplication();
                        d.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                        duplications.add(d);
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
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Push) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Push> response = new ArrayList<>();
        for (JsonObject object : PushDTO.findAll(r)) {
            response.add(new PushDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Push> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Push) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Push) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        List<Push> response = new ArrayList<>();
        for (JsonObject object : PushDTO.findAll(r)) {
            response.add(
                    queryById(
                            new PushDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Push> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat = "MATCH (n:Push) WHERE ";

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

        List<Push> response = new ArrayList<>();
        for (JsonObject object : PushDTO.findAll(r)) {
            response.add(
                    queryById(
                            new PushDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Push queryBySameId(Push push) throws ConnectException, IndexOutOfBoundsException {
        return queryById(push.getId());
    }

    public List<Push> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:pushed_to]->(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                id
        );

        List<Push> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new PushDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public Push queryByIssue(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:has_issue]->(b:Issue) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                id
        );

        Push response = new PushDTO().createFromNeo4jData(PushDTO.findFirst(responseString)).toModel();

        return response;
    }

    public Push queryByCommit(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:contains_commit]->(b:Commit) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                id
        );

        Push response = new PushDTO().createFromNeo4jData(PushDTO.findFirst(responseString)).toModel();

        return response;
    }

    public Push queryByDuplication(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:has_duplication]->(b:Duplication) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                id
        );

        Push response = new PushDTO().createFromNeo4jData(PushDTO.findFirst(responseString)).toModel();

        return response;
    }

    @Override
    public int create(Push push) throws ConnectException, IndexOutOfBoundsException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Push { key: '%s' }) RETURN {id:id(n), labels: labels(n), data: n} ",
                push.getKey()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Push createIfNotExists(Push data) throws ConnectException, IndexOutOfBoundsException {
        Push push = data.getId() != null ? queryById(data.getId()) : null;
        if (push == null || !push.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("key", data.getKey()).get(0);
        } else return push;
    }

    @Override
    public int update(Push push) throws ConnectException, IndexOutOfBoundsException {
        if(push != null && queryById(push.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Push) " +
                            "WHERE ID(n) = %d " +
                            "SET n.key = '%s', n.score = %d, n.timeStamp = %d " +
                            "RETURN {id:id(n), labels: labels(n), data: n} ",
                    push.getId(),
                    push.getKey(),
                    push.getScore(),
                    push.getTimestamp()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Push is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Push push) throws ConnectException, IndexOutOfBoundsException {
        return deleteById(push.getId());
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Push> pushes) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Push push : pushes)
            changed += delete(push);
        return changed;
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Long id : ids)
            changed += deleteById(id);
        return changed;
    }

    public int saveRelationship(Push push, Commit commit) {
        return 0;
    }

    public int saveRelationship(Push push, Duplication duplication) {
        return 0;
    }

    public int saveRelationship(Push push, Issue issue) {
        return 0;
    }

    public int saveRelationship(Push push, Project project) {
        return 0;
    }
}
