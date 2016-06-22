package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.PushDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.*;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.*;

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
        Project p = null;
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
                    case "Project" :
                        p = new Project();
                        p.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }
        if(dto == null) return null;

        dto.commits = commits;
        dto.issues = issues;
        dto.duplications = duplications;
        dto.project = p;

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
                "MATCH (a:Push)-[:%s]->(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.PUSHED_TO.name(), id
        );

        List<Push> response = new ArrayList<>();
        for (JsonObject object : PushDTO.findAll(responseString)) {
            response.add(new PushDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public Push queryByIssue(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:%s]->(b:Issue) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.HAS_ISSUE.name(), id
        );

        Push response = new PushDTO().createFromNeo4jData(PushDTO.findFirst(responseString)).toModel();

        return response;
    }

    public Push queryByCommit(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:%s]->(b:Commit) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.CONTAINS_COMMIT.name(), id
        );

        Push response = new PushDTO().createFromNeo4jData(PushDTO.findFirst(responseString)).toModel();

        return response;
    }

    public List<Push> queryByUser(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)<-[:%s]-(b:User) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                User.Relations.HAS_PUSHED.name(), id
        );

        List<Push> response = new ArrayList<>();
        for (JsonObject object : PushDTO.findAll(responseString)) {
            response.add(new PushDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public Push queryByDuplication(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push)-[:has_duplication]->(b:Duplication) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.HAS_DUPLICATION.name(), id
        );

        Push response = new PushDTO().createFromNeo4jData(PushDTO.findFirst(responseString)).toModel();

        return response;
    }

    @Override
    public int create(Push push) throws ConnectException, IndexOutOfBoundsException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Push { key: '%s', timestamp: %d, score: %s } ) RETURN {id:id(n), labels: labels(n), data: n} ",
//                "CREATE (n:Push { key: '%s' }) RETURN {id:id(n), labels: labels(n), data: n} ",
                push.getKey(),
                push.getTimestamp(),
                new Formatter(Locale.US).format("%.2f", push.getScore())
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
                            "SET n.key = '%s', n.score = %f, n.timeStamp = %d " +
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

    public int saveRelationship(Push push, Commit commit) throws ConnectException {
        if (push.getId() == null || commit.getId() == null) {
            L.e("Id from push or commit was null: push[%b], commit[%b]",
                    push.getId()==null, commit.getId()==null);
            return 0;
        }
        L.d("Creating relationship between push: '%d' and commit: '%d'",
                push.getId(), commit.getId());

        String response = createRelationship(push.getId(), commit.getId(), Push.Relations.CONTAINS_COMMIT);

        return new JsonParser().parse(response).getAsJsonObject().get("errors").getAsJsonArray().size() == 0 ? 1 : 0;
    }

    public int saveRelationship(Push push, Duplication duplication) throws ConnectException {
        if (push.getId() == null || duplication.getId() == null) {
            L.e("Id from push or duplication was null: push[%b], duplication[%b]",
                    push.getId()==null, duplication.getId()==null);
            return 0;
        }
        L.d("Creating relationship between push: '%d' and duplication: '%d'",
                push.getId(), duplication.getId());

        String response = createRelationship(push.getId(), duplication.getId(), Push.Relations.HAS_DUPLICATION);

        return new JsonParser().parse(response).getAsJsonObject().get("errors").getAsJsonArray().size() == 0 ? 1 : 0;
    }

    public int saveRelationship(Push push, Issue issue) throws ConnectException {
        if (push.getId() == null || issue.getId() == null) {
            L.e("Id from push or issue was null: push[%b], issue[%b]",
                    push.getId()==null, issue.getId()==null);
            return 0;
        }
        L.d("Creating relationship between push: '%d' and issue: '%d'",
                push.getId(), issue.getId());

        String response = createRelationship(push.getId(), issue.getId(), Push.Relations.HAS_ISSUE);

        return new JsonParser().parse(response).getAsJsonObject().get("errors").getAsJsonArray().size() == 0 ? 1 : 0;
    }

    public int saveRelationship(Push push, Project project) throws ConnectException {
        if (push.getId() == null || project.getId() == null) {
            L.e("Id from push or project was null: push[%b], project[%b]",
                    push.getId()==null, project.getId()==null);
            return 0;
        }
        L.d("Creating relationship between push: '%d' and project: '%d'",
                push.getId(), project.getId());

        String response = createRelationship(push.getId(), project.getId(), Push.Relations.PUSHED_TO);

        return new JsonParser().parse(response).getAsJsonObject().get("errors").getAsJsonArray().size() == 0 ? 1 : 0;
    }
}
