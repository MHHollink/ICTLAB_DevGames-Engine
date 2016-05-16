package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.connection.database.dto.PushDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
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

public class UserDao implements Dao<User, Long> {

    @Override
    public User queryForId(Long id) throws ConnectException, IndexOutOfBoundsException {
        UserDTO dto = null; Set<Project> projects = new HashSet<>(); Set<Push> pushes = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                            "MATCH (a:User)-[r]->(b) " +
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
                    case "User" :
                        if(dto == null) dto = new UserDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            UserDTO uTemp = new UserDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(uTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, uTemp);
                        }
                        break;
                    case "Project" :
                        projects.add(new ProjectDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    case "Push" :
                        pushes.add(new PushDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }
        if(dto == null) return null;
        dto.projects = projects; dto.pushes = pushes;
        return dto.toModel();
    }

    @Override
    public List<User> queryForAll() throws ConnectException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) RETURN n"
        );

        List<User> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(r)) {
            response.add(new UserDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<User> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:User) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:User) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                            queryFormat,
                            fieldName,
                            value
        );

        List<User> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(r)) {
            response.add(
                    queryForId(
                            new UserDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<User> queryByFields(Map<String, Object> fieldValues) throws ConnectException {
        String queryFormat = "MATCH (n:User) WHERE ";

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

        List<User> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(r)) {
            response.add(
                    queryForId(
                            new UserDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public User queryForSameId(User user) throws ConnectException {
        return queryForId(user.getId());
    }

    @Override
    public int create(User data) throws ConnectException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:User { username: '%s', gitUsername: '%s', firstName: '%s', lastName: '%s', age: %d, mainJob: '%s', password: '%s', gcmRegId: '%s' }) RETURN n ",
                data.getUsername(),
                data.getGitUsername(),
                data.getFirstName(),
                data.getLastName(),
                data.getAge(),
                data.getMainJob(),
                data.getPassword(),
                data.getGcmId());

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public User createIfNotExists(User data) throws ConnectException {
        User user = queryForId(data.getId());
        if (user == null || !user.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return data;
        } else return user;
    }

    @Override
    public int update(User user) throws ConnectException {
        if(user != null && queryForId(user.getId()) != null) {

            // TODO: 16-5-2016
            return 0;

            // return 1;
        }
        L.w("User is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(User user) throws ConnectException {
        return deleteById(user.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException {
        if(queryForId(id) == null) return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) " +
                        "WHERE ID(n) = %d " +
                        "SET n.username = NULL AND n.password = NULL AND " +
                            "n.firstName = NULL AND n.lastName = NULL AND n.age = NULL " + // TODO: 16-5-2016 More field to null?
                        "RETURN n",
                id
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public int delete(Collection<User> users) throws ConnectException {
        int changed = 0;
        for(User user : users)
            changed += delete(user);
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
