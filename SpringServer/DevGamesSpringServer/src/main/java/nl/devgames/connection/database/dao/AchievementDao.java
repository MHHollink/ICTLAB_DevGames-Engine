package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.AchievementDTO;
import nl.devgames.model.Achievement;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.*;

/**
 * Created by Jorikito on 22-Jun-16.
 */
public class AchievementDao  extends AbsDao<Achievement, Long> {
    @Override
    public Achievement queryById(Long id) throws ConnectException {
        AchievementDTO dto = null;
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Achievement) WHERE ID(n) = %d RETURN {id:id(n), labels: labels(n), data: n}", id
        );

        dto = new AchievementDTO().createFromNeo4jData(AchievementDTO.findFirst(responseString));

        return dto.toModel();
    }

    @Override
    public List<Achievement> queryForAll() throws ConnectException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Achievement) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Achievement> response = new ArrayList<>();
        for (JsonObject object : AchievementDTO.findAll(r)) {
            response.add(new AchievementDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Achievement> queryByField(String fieldName, Object value) throws ConnectException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Achievement) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Achievement) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        List<Achievement> response = new ArrayList<>();
        for (JsonObject object : AchievementDTO.findAll(r)) {
            response.add(
                    queryById(
                            new AchievementDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Achievement> queryByFields(Map<String, Object> fieldValues) throws ConnectException {
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

        List<Achievement> response = new ArrayList<>();
        for (JsonObject object : AchievementDTO.findAll(r)) {
            response.add(
                    queryById(
                            new AchievementDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Achievement queryBySameId(Achievement achievement) throws ConnectException {
        return queryById(achievement.getId());
    }

    @Override
    public int create(Achievement achievement) throws ConnectException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Achievement { type: '%s', generatedUUID: '%s' }) RETURN {id:id(n), labels: labels(n), data: n} ",
                achievement.getType(),
                achievement.getUuid()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Achievement createIfNotExists(Achievement data) throws ConnectException {
        Achievement achievement = data.getId() != null ? queryById(data.getId()) : null;
        if (achievement == null || !achievement.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("gemeratedUUID", data.getUuid()).get(0);
        } else return achievement;
    }

    @Override
    public int update(Achievement achievement) throws ConnectException {
        if(achievement != null && queryById(achievement.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Achievement) " +
                            "WHERE ID(n) = %d " +
                            "SET n.type = '%s' " +
                            "RETURN {id:id(n), labels: labels(n), data: n} ",
                    achievement.getId(),
                    achievement.getType()
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
    public int delete(Achievement achievement) throws ConnectException {
        L.d("Deleting achievement: '%s'", achievement);
        return deleteById(achievement.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException {
        if(queryById(id) == null) return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Achievement) " +
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
    public int delete(Collection<Achievement> achievements) throws ConnectException {
        int changed = 0;
        for(Achievement achievement : achievements)
            changed += delete(achievement);
        return changed;
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws ConnectException {
        int changed = 0;
        for(Long id : ids)
            changed += deleteById(id);
        return changed;
    }


}
