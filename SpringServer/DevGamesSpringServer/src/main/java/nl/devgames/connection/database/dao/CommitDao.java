package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.CommitDTO;
import nl.devgames.model.Commit;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorikito on 18-May-16.
 */
public class CommitDao extends AbsDao<Commit, Long>  {

    @Override
    public Commit queryById(Long id) throws ConnectException, IndexOutOfBoundsException {
        CommitDTO dto = null;
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Commit) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        dto = new CommitDTO().createFromNeo4jData(CommitDTO.findFirst(responseString));

        return dto.toModel();
    }

    @Override
    public List<Commit> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Commit) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(r)) {
            response.add(new CommitDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Commit> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Commit) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Commit) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(r)) {
            response.add(
                    queryById(
                            new CommitDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Commit> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat = "MATCH (n:Commit) WHERE ";

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

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(r)) {
            response.add(
                    queryById(
                            new CommitDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Commit queryBySameId(Commit commit) throws ConnectException, IndexOutOfBoundsException {
        return queryById(commit.getId());
    }

    public List<Commit> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Commit)<-[:contains_commits]-(b:Push)-[:pushed_to]->(c:Project) " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a} ",
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
                        "RETURN {id:id(a), labels: labels(a), data: a} ",
                id
        );

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(responseString)) {
            response.add(new CommitDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public List<Commit> queryByUser(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Commit)<-[:contains_commits]-(b:Push)<-[:pushed_by]-(c:User) " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a} ",
                id
        );

        List<Commit> response = new ArrayList<>();
        for (JsonObject object : CommitDTO.findAll(responseString)) {
            response.add(new CommitDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Commit commit) throws ConnectException, IndexOutOfBoundsException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Commit { commitId: '%s', commitMsg: '%s', timestamp: %d }) RETURN {id:id(n), labels: labels(n), data: n} ",
                commit.getCommitId(),
                commit.getCommitMsg(),
                commit.getTimeStamp()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Commit createIfNotExists(Commit data) throws ConnectException, IndexOutOfBoundsException {
        Commit commit = data.getId() != null ? queryById(data.getId()) : null;
        if (commit == null || !commit.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("commitId", data.getCommitId()).get(0);
        } else return commit;
    }

    @Override
    public int update(Commit commit) throws ConnectException, IndexOutOfBoundsException {
        if(commit != null && queryById(commit.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Commit) " +
                            "WHERE ID(n) = %d " +
                            "SET n.commitMsg = '%s', n.timeStamp = %d " +
                            "RETURN {id:id(n), labels: labels(n), data: n} ",
                    commit.getId(),
                    commit.getCommitMsg(),
                    commit.getTimeStamp()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Commit is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Commit commit) throws ConnectException, IndexOutOfBoundsException {
        L.i("Deleting commit: '%s'", commit);
        return deleteById(commit.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException, IndexOutOfBoundsException {
        if(queryById(id) == null) return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Commit) " +
                        "WHERE ID(n) = %d " +
                        "SET n.name = NULL, n.description = NULL " +
                        "RETURN {id:id(n), labels: labels(n), data: n} ",
                id
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public int delete(Collection<Commit> commits) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Commit commit : commits)
            changed += delete(commit);
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
