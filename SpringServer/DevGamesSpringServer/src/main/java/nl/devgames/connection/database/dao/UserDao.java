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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDao implements Dao<User, Long> {

    /**
     * Returns one complete user with all the outgoing relations;
     *
     *      {@link User#pushes}, {@link User#projects}
     *
     *
     * @param id
     * @return
     */
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
    public List<User> queryForAll() {
        return null;
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
            response.add(new UserDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<User> queryByFields(Map<String, Object> fieldValues) {
        return null;
    }

    @Override
    public User queryForSameId(User data) throws ConnectException {
        return new UserDTO()
                .createFromNeo4jData(
                        UserDTO.findFirst(
                                Neo4JRestService
                                        .getInstance()
                                        .postQuery(
                                                "MATCH (n:User) " +
                                                        "WHERE ID(n) = %d " +
                                                        "RETURN {id:id(n), labels: labels(n), data: n}",
                                                data.getId()
                                        )
                        )
                )
                .toModel();
    }

    @Override
    public int create(User data) throws ConnectException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:User { username: '%s', gitUsername: '%s', firstName: '%s', lastName: '%s', age: %d, mainJob: '%s', password: '%s', gcmRegId: '%s' }) ",
                data.getUsername(),
                data.getGitUsername(),
                data.getFirstName(),
                data.getLastName(),
                data.getAge(),
                data.getMainJob(),
                data.getPassword(),
                data.getGcmId());

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();



        return 0;
    }

    @Override
    public User createIfNotExists(User data) {
        return null;
    }

    @Override
    public int update(User data) {
        return 0;
    }

    @Override
    public int delete(User data) {
        return 0;
    }

    @Override
    public int deleteById(Long aLong) {
        return 0;
    }

    @Override
    public int delete(Collection<User> datas) {
        return 0;
    }

    @Override
    public int deleteIds(Collection<Long> longs) {
        return 0;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public User next() {
        return null;
    }
}
