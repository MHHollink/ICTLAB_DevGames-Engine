package nl.devgames.connection.database.dao;

import com.google.gson.*;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.SettingsDTO;
import nl.devgames.model.Project;
import nl.devgames.model.Settings;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.*;

/**
 * Created by Jorikito on 02-Jun-16.
 */
public class SettingsDao extends AbsDao<Settings, Long>  {
    @Override
    public Settings queryById(Long id) throws ConnectException {
        if(id == null ) return  null;
        L.d("Query issue with id: %d", id);
        SettingsDTO dto = null;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Settings) " +
                        "WHERE ID(a) = %d " +
                        "OPTIONAL " +
                        "MATCH a-[]->(b) " +
                        "WHERE ID(a) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}," +
                        "{id:id(b), labels: labels(b)}",
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
                String label = labels.getAsJsonArray().get(0).getAsString();

                switch (label) {
                    case "Settings" :
                        if(dto == null)
                            dto = new SettingsDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            SettingsDTO sTemp = new SettingsDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(sTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, sTemp);
                        }
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }
        if(dto == null)
            return null;
        return dto.toModel();
    }

    @Override
    public List<Settings> queryForAll() throws ConnectException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Settings) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Settings> response = new ArrayList<>();
        for (JsonObject object : SettingsDTO.findAll(r)) {
            response.add(new SettingsDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Settings> queryByField(String fieldName, Object value) throws ConnectException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Settings) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Settings) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        List<Settings> response = new ArrayList<>();
        for (JsonObject object : SettingsDTO.findAll(r)) {
            response.add(
                    queryById(
                            new SettingsDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Settings> queryByFields(Map<String, Object> fieldValues) throws ConnectException {
        String queryFormat = "MATCH (n:Settings) WHERE ";

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

        List<Settings> response = new ArrayList<>();
        for (JsonObject object : SettingsDTO.findAll(r)) {
            response.add(
                    queryById(
                            new SettingsDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Settings queryBySameId(Settings settings) throws ConnectException {
        return queryById(settings.getId());
    }

    public Settings queryByProject (long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Settings)<-[:%s]-(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                Project.Relations.HAS_SETTINGS.name(), id
        );

        return new SettingsDTO().createFromNeo4jData(SettingsDTO.findFirst(responseString)).toModel();
    }

    @Override
    public int create(Settings settings) throws ConnectException {
        L.d("Creating settings: %s", settings);
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Settings { generatedUUID: '%s', startScore: %s, issuesPerCommitThreshold: %s, pointStealing: %b, negativeScore: %b }) " +
                        "RETURN {id:id(n), labels: labels(n), data: n} ",
                settings.getUuid(),
                new Formatter(Locale.US).format("%.2f", settings.getStartScore()),
                new Formatter(Locale.US).format("%.2f", settings.getIssuesPerCommitThreshold()),
                settings.isPointStealing(),
                settings.isNegativeScores()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Settings createIfNotExists(Settings data) throws ConnectException {
        Settings settings = data.getId() != null ? queryById(data.getId()) : null;
        if (settings == null || !settings.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("generatedUUID", data.getUuid()).get(0);
        } else return settings;
    }

    @Override
    public int update(Settings settings) throws ConnectException {
        if(settings != null && queryById(settings.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Settings) " +
                            "WHERE ID(n) = %d " +
                            "SET n.startScore = %f, n.issuesPerCommitThreshold = %f," +
                            "s.pointStealing: %b, negativeScore: %b " +
                            "RETURN {id:id(n), labels: labels(n), data: n} ",
                    settings.getStartScore(),
                    settings.getIssuesPerCommitThreshold(),
                    settings.isPointStealing(),
                    settings.isNegativeScores()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Settings is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Settings settings) throws ConnectException {
        return deleteById(settings.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException {
        if(queryById(id) == null) return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Settings) " +
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
    public int delete(Collection<Settings> settingsCollection) throws ConnectException {
        int changed = 0;
        for(Settings settings : settingsCollection)
            changed += delete(settings);
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
