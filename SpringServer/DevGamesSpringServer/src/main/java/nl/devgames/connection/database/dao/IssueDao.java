package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.IssueDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Issue;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jorikito on 18-May-16.
 */
public class IssueDao extends AbsDao<Issue, Long> {


    @Override
    public Issue queryById(Long id) throws ConnectException, IndexOutOfBoundsException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Issue) WHERE ID(n) = %d RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        JsonObject json = new JsonParser().parse(responseString).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());

        JsonArray data = json.get("results")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("data")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("row")
                .getAsJsonArray();

        return new IssueDTO().createFromNeo4jData(data.get(0).getAsJsonObject()).toModel();
    }

    @Override
    public List<Issue> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Issue) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : IssueDTO.findAll(r)) {
            response.add(new IssueDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Issue> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Issue) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Issue) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        return IssueDTO.findAll(r).stream().map(
                o -> {
                    try {
                        return queryById(o.get("id").getAsLong());
                    } catch (ConnectException e) {
                        L.e(e, "database offline");
                        throw new DatabaseOfflineException();
                    }
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<Issue> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat = "MATCH (n:Issue) WHERE ";

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

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : IssueDTO.findAll(r)) {
            response.add(
                    queryById(
                            new IssueDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Issue queryBySameId(Issue issue) throws ConnectException, IndexOutOfBoundsException {
        return queryById(issue.getId());
    }

    public List<Issue> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Issue)<-[:%s]-(b:Push)-[:%s]->(c:Project) " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.HAS_ISSUE.name(), Push.Relations.PUSHED_TO.name(), id
        );

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new IssueDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public List<Issue> getIssuesFromPush(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Issue)<-[:%s]-(b:Push) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.HAS_ISSUE.name(), id
        );

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new IssueDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public List<Issue> queryByUser(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Issue)<-[:%s]-(b:Push)<-[:%s]-(c:User) " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Push.Relations.HAS_ISSUE.name(), User.Relations.HAS_PUSHED.name(), id
        );

        List<Issue> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new IssueDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Issue issue) throws ConnectException, IndexOutOfBoundsException {

        issue.setMessage(
                issue.getMessage().replace("\'", "|").replace("\\", "/").replace("\"", "\\\"")
        );

        String query = String.format(
                "CREATE (" +
                            "n:Issue {" +
                                "key: '%s', severity: '%s', component: '%s', startLine: %d, endLine: %d, status: '%s', resolution: '%s', message: '%s', debt: %d, creationDate: %d, updateDate: %d, closeDate: %d" +
                            "}" +
                        ") " +
                        "RETURN {id: id(n), labels: labels(n), data: n} ",
                issue.getKey(),
                issue.getSeverity(),
                issue.getComponent(),
                issue.getStartLine(),
                issue.getEndLine(),
                issue.getStatus(),
                issue.getResolution(),
                issue.getMessage(),
                issue.getDebt(),
                issue.getCreationDate(),
                issue.getUpdateDate(),
                issue.getCloseDate()
        );

        String response = Neo4JRestService.getInstance().postQuery(
                query
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0) {
            L.e(query);
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        }
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Issue createIfNotExists(Issue data) throws ConnectException, IndexOutOfBoundsException {
        Issue issue = data.getId() != null ? queryById(data.getId()) : null;
        if (issue == null || !issue.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("key", data.getKey()).get(0);
        } else return issue;
    }

    @Override
    public int update(Issue issue) throws ConnectException, IndexOutOfBoundsException {
        if(issue != null && queryById(issue.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Issue) " +
                            "WHERE ID(n) = %d " +
                            "SET key: %s, severity: '%s', " +
                            "component: '%s', startLine: %d, endLine: %d, " +
                            "status: '%s', resolution: '%s', message: '%s', " +
                            "debt: %d, creationDate: %d, updateDate %d, closeDate %d " +
                    "RETURN {id:id(n), labels: labels(n), data: n} ",
                    issue.getId(),
                    issue.getKey(),
                    issue.getSeverity(),
                    issue.getComponent(),
                    issue.getStartLine(),
                    issue.getEndLine(),
                    issue.getStatus(),
                    issue.getResolution(),
                    issue.getMessage(),
                    issue.getDebt(),
                    issue.getCreationDate(),
                    issue.getUpdateDate(),
                    issue.getCloseDate()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Issue is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Issue issue) throws ConnectException, IndexOutOfBoundsException {
        return deleteById(issue.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException, IndexOutOfBoundsException {
        if(queryById(id) == null) return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Issue) " +
                        "WHERE ID(n) = %d " +
                        "OPTIONAL MATCH n-[r]-() DELETE n, r " +
                        "RETURN {id:id(n), labels: labels(n), data: n} ",
                id
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public int delete(Collection<Issue> issues) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Issue issue : issues)
            changed += delete(issue);
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
