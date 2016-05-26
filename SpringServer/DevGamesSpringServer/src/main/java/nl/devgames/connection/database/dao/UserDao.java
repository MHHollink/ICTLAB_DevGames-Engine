package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDao extends AbsDao<User, Long> {

    @Override
    public User queryById(Long id) throws ConnectException {
        if(id == null ) return  null;
        L.d("Query user with id: %d", id);
        UserDTO dto = null;
        Set<Project> projects = new HashSet<>();
        Set<Push> pushes = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                            "MATCH (a:User) " +
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
                    case "User" :
                        if(dto == null)
                            dto = new UserDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            UserDTO uTemp = new UserDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(uTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, uTemp);
                        }
                        break;
                    case "Project" :
                        Project project = new Project();
                        project.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                        projects.add(project);
                        break;
                    case "Push" :
                        Push push = new Push();
                        push.setId(
                                row.getAsJsonObject().get("id").getAsLong()
                        );
                        pushes.add(push);
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }
        if(dto == null)
            return null;
        dto.projects = projects;
        dto.pushes = pushes;
        return dto.toModel();
    }

    public List<String> userTokensFromProject(long userId, long projectId) throws ConnectException {
        List<String> tokenList = new ArrayList<>();

        String stringResponse = Neo4JRestService.getInstance().postQuery(
                "MATCH (u:User)-[:works_on]->(p:Project) " +
                        "WHERE ID(u) = %d AND ID(p) = %d " +
                        "RETURN {id:id(u), labels: labels(u), data: u}",
                userId,
                projectId
        );

        for (JsonObject object : UserDTO.findAll(stringResponse)) {
            UserDTO userDTO = new UserDTO().createFromNeo4jData(object);
            tokenList.add(userDTO.toModel().getGcmId());
        }
        return tokenList;
    }

    public User getPusherOfPush(long id) throws ConnectException {
        List<User> userList = new ArrayList<>();

        String stringResponse = Neo4JRestService.getInstance().postQuery(
                "MATCH (u:User)-[:%s]->(p:Push) " +
                        "WHERE ID(p) = %d " +
                        "RETURN {id:id(u), labels: labels(u), data: u}",
                User.Relations.HAS_PUSHED.name(), id
        );

        return new UserDTO().createFromNeo4jData(UserDTO.findFirst(stringResponse)).toModel();
    }

    public User queryByCommit(long id) throws ConnectException {

        String stringResponse = Neo4JRestService.getInstance().postQuery(
                "MATCH (u:User)-[:%s]->(p:Push)-[:%s]->(c:Commit) " +
                        "WHERE ID(c) = %d " +
                        "RETURN {id:id(u), labels: labels(u), data: u}",
                User.Relations.HAS_PUSHED.name(), Push.Relations.CONTAINS_COMMIT.name(), id
        );

        return new UserDTO().createFromNeo4jData(UserDTO.findFirst(stringResponse)).toModel();
    }

    public User queryByIssue(long id) throws ConnectException {

        String stringResponse = Neo4JRestService.getInstance().postQuery(
                "MATCH (u:User)-[:%s]->(p:Push)-[:%s]->(i:Issue) " +
                        "WHERE ID(i) = %d " +
                        "RETURN {id:id(u), labels: labels(u), data: u}",
                User.Relations.HAS_PUSHED.name(), Push.Relations.HAS_ISSUE.name(), id
        );

        return new UserDTO().createFromNeo4jData(UserDTO.findFirst(stringResponse)).toModel();
    }

    public User queryByDuplication(long id) throws ConnectException {

        String stringResponse = Neo4JRestService.getInstance().postQuery(
                "MATCH (u:User)-[:%s]->(p:Push)-[:%s]->(d:Duplication) " +
                        "WHERE ID(d) = %d " +
                        "RETURN {id:id(u), labels: labels(u), data: u}",
                User.Relations.HAS_PUSHED.name(), Push.Relations.HAS_DUPLICATION.name(), id
        );

        return new UserDTO().createFromNeo4jData(UserDTO.findFirst(stringResponse)).toModel();
    }

    @Override
    public List<User> queryForAll() throws ConnectException {
        L.d("Query user all users");
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) RETURN {id:id(u), labels: labels(u), data: u}"
        );

        List<User> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(r)) {
            response.add(new UserDTO().createFromJsonObject(object).toModel());
        }
        return response;
    }

    @Override
    public List<User> queryByField(String fieldName, Object value) throws ConnectException {
        L.d("Query users with %s: %s", fieldName, value);
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:User) WHERE n.%s = %s RETURN {id:id(n), labels: labels(n), data: n}";
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
                    queryById(
                            new UserDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<User> queryByFields(Map<String, Object> fieldValues) throws ConnectException {
        L.d("Query users with fields: %s", fieldValues.toString());
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
                    queryById(
                            new UserDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public User queryBySameId(User user) throws ConnectException {
        L.d("Query user with same id as user: %s", user);
        return queryById(user.getId());
    }

    public List<User> queryByProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:User)-[:%s]->(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                User.Relations.IS_DEVELOPING.name(), id
        );

        List<User> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new UserDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    public List<User> queryByBusiness(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:User)-[:%s]->(b:Business) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                id, User.Relations.IS_DEVELOPING.name()
        );
        //todo make new relationship

        List<User> response = new ArrayList<>();
        for (JsonObject object : UserDTO.findAll(responseString)) {
            response.add(new UserDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(User user) throws ConnectException {
        L.d("Creating user: %s", user);
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:User { username: '%s', gitUsername: '%s', firstName: '%s', lastName: '%s', age: %d, mainJob: '%s', password: '%s', gcmId: '%s' }) RETURN n ",
                user.getUsername(),
                user.getGitUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getMainJob(),
                user.getPassword(),
                user.getGcmId()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public User createIfNotExists(User user) throws ConnectException {
        L.d("Creating user if it does not exist: %s", user);

        Map<String, Object> fields = new HashMap<>();
        fields.put("username", user.getUsername());
        fields.put("gitUsername", user.getGitUsername());

        List<User> u = queryByFields(fields);
        if (u.size() != 0) {
            int inserted = create(user);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("username",user.getUsername()).get(0);
        } else return u.get(0);
    }

    @Override
    public int update(User user) throws ConnectException {
        L.d("Updating user: %s", user);
        if(user != null && queryById(user.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:User) " +
                            "WHERE ID(n) = %d " +
                            "SET n.username = '%s', n.password = '%s', n.firstName = '%s', n.lastName = '%s', n.age = %d, " +
                                "n.gcmId = '%s', n.session = '%s', n.mainJob = '%s', n.gitUsername = '%s' " +
                            "RETURN {id:id(n), labels: labels(n), data: n}",
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getGcmId(),
                    user.getSessionId(),
                    user.getMainJob(),
                    user.getGitUsername()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("User is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(User user) throws ConnectException {
        L.d("Deleting user: %s", user);
        return deleteById(user.getId());
    }

    @Override
    public int deleteById(Long id) throws ConnectException {
        L.d("Deleting user with id: %d", id);
        if(queryById(id) == null)
            return 0;
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) " +
                        "WHERE ID(n) = %d " +
                        "SET n.username = NULL, n.password = NULL, " +
                            "n.firstName = NULL, n.lastName = NULL, n.age = NULL, n.session = NULL, n.gcmId = NULL, n.deleted = true " + // TODO: 16-5-2016 More field to null?
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public int delete(Collection<User> users) throws ConnectException {
        L.d("Deleting users: %s", users);
        int changed = 0;
        for(User user : users)
            changed += delete(user);
        return changed;
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws ConnectException {
        L.d("Deleting users with ids: %d", ids);
        int changed = 0;
        for(Long id : ids)
            changed += deleteById(id);
        return changed;
    }

    /**
     * This method is used to create a relationship between a User and a Project. The id's from both parameters are used in the qeury.
     *
     * The objects returned from creating an object with {@link #createIfNotExists(User)} and {@link ProjectDao#createIfNotExists(Project)} should have a valid ID
     *
     * @param user
     * @param project
     * @return
     * @throws ConnectException
     */
    public int saveRelationship(User user, Project project) throws ConnectException {
        if (user.getId() == null || project.getId() == null) {
            L.e("Id from user or project was null: user[%b], project[%b]",
                    user.getId()==null, project.getId()==null);
            return 0;
        }
        L.d("Creating relationship between user: '%d' and project: '%d'",
                user.getId(), project.getId());

        String response = createRelationship(user.getId(), project.getId(), User.Relations.IS_DEVELOPING);

        return new JsonParser().parse(response).getAsJsonObject().get("errors").getAsJsonArray().size() == 0 ? 1 : 0;
    }

    /**
     * This method is used to create a relationship between a User and a Project. The id's from both parameters are used in the qeury.
     *
     * The objects returned from creating an object with {@link #createIfNotExists(User)} and {@link PushDao#createIfNotExists(Push)} should have a valid ID
     *
     * @param user
     * @param push
     * @return
     * @throws ConnectException
     */
    public int saveRelationship(User user, Push push) throws ConnectException {
        if (user.getId() == null || push.getId() == null) {
            L.e("Id from user or push was null: user[%b], push[%b]",
                    user.getId()==null, push.getId()==null);
            return 0;
        }
        L.d("Creating relationship between user: %d and push: %d",
                user.getId(), push.getId());

        String response = createRelationship(user.getId(), push.getId(), User.Relations.HAS_PUSHED);

        return new JsonParser().parse(response).getAsJsonObject().get("errors").getAsJsonArray().size() == 0 ? 1 : 0;
    }


}
