package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectDao extends AbsDao<Project, Long> {

    @Override
    public Project queryById(Long id) throws ConnectException, IndexOutOfBoundsException {
        ProjectDTO dto = null;
        User creator = null;
        Set<User> developers = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Project) " +
                        "WHERE ID(a) = %d " +
                            "OPTIONAL " +
                                "MATCH a-[]->(b) " +
                                "WHERE ID(a) = %d " +
                        "RETURN " +
                            "{id:id(a), labels: labels(a), data: a}," +
                            "{id:id(b), labels: labels(b)}",
                id, id
        );

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(response).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0) {
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return null;
        }

        JsonArray data = json.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();
        for (JsonElement element : data) {
            JsonArray rows = element.getAsJsonObject().get("row").getAsJsonArray();

            for ( JsonElement row : rows) {
                JsonElement labels = row.getAsJsonObject().get("labels");
                if (labels instanceof JsonNull)
                    continue;
                String label = row.getAsJsonObject().get("labels").getAsJsonArray().get(0).getAsString();

                switch (label) {
                    case "Project" :
                        if(dto == null) dto = new ProjectDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            ProjectDTO pTemp = new ProjectDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(pTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, pTemp);
                        }
                        break;
                    case "User" :
                        creator = new User();
                        creator.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }

        response = Neo4JRestService.getInstance().postQuery(
                "match (a:Project),(b:User) WHERE ID(a) = %d MATCH a<-[]-b RETURN id(b)", id
        );

        json = parser.parse(response).getAsJsonObject();

        if(json.get("errors").getAsJsonArray().size() != 0) {
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return null;
        }

        json.get("results")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("data")
                .getAsJsonArray()
                .forEach(row -> {
                    User u = new User();
                    u.setId(
                            row.getAsJsonObject()
                                    .get("row")
                                    .getAsJsonArray()
                                    .get(0)
                                    .getAsLong()
                    );
                    developers.add(u);
                });


        if(dto == null) return null;

        dto.creator = creator;
        dto.developers = developers;

        return dto.toModel();
    }

    @Override
    public List<Project> queryForAll() throws ConnectException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Project) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Project> response = new ArrayList<>();
        for (JsonObject object : ProjectDTO.findAll(r)) {
            response.add(new ProjectDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Project> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Project) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Project) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                            queryFormat,
                            fieldName,
                            value
        );

        List<Project> response = new ArrayList<>();
        for (JsonObject object : ProjectDTO.findAll(r)) {
            response.add(
                    queryById(
                            new ProjectDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Project> queryByFields(Map<String, Object> fieldValues) throws ConnectException {
        String queryFormat = "MATCH (n:Project) WHERE ";

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

        List<Project> response = new ArrayList<>();
        for (JsonObject object : ProjectDTO.findAll(r)) {
            response.add(
                    queryById(
                            new ProjectDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Deprecated
    public int addUserToProject(long userId, long projectId) throws ConnectException {
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User), (m:Project) " +
                        "WHERE ID(n) = %d AND ID(m) = %d " +
                        "CREATE (n)-[:works_on]->(m)" +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                userId,
                projectId
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0) {
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return 0;
        }
        else {
            return json.get("results").getAsJsonArray().size();
        }
    }

    public Project getProjectByPush(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Project)<-[:pushed_to]-(b:Push) " +
                        "WHERE ID(b) = %d " +
                        "RETURN a",
                id
        );

        return new ProjectDTO().createFromNeo4jData(ProjectDTO.findFirst(responseString)).toModel();
    }


    @Override
    public Project queryBySameId(Project project) throws ConnectException {
        return queryById(project.getId());
    }

    @Override
    public int create(Project data) throws ConnectException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Project { name: '%s', description: '%s' }) RETURN n ",
                data.getName(),
                data.getDescription()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Project createIfNotExists(Project data) throws ConnectException {
        Project project = data.getId() != null ? queryById(data.getId()) : null;
        if (project == null || !project.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("name", data.getName()).get(0);
        } else return project;
    }

    @Override
    public int update(Project project) throws ConnectException {
        if(project != null && queryById(project.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Project) " +
                            "WHERE ID(n) = %d " +
                            "SET n.name = '%s', n.description = '%s' " +
                            "RETURN n",
                    project.getId(),
                    project.getName(),
                    project.getDescription()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Project is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Project project) throws ConnectException {
        return deleteById(project.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException {
        if(queryById(id) == null) return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Project) " +
                        "WHERE ID(n) = %d " +
                        "SET n.name = NULL, n.description = NULL " +
                        "RETURN n",
                id
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public int delete(Collection<Project> projects) throws ConnectException {
        int changed = 0;
        for(Project project : projects)
            changed += delete(project);
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
