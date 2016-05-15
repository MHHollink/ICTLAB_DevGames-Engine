package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.User;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserDao implements Dao<User, Long> {

    @Override
    public User queryForId(Long aLong) {
        try {
            return new UserDTO()
                    .createFromNeo4jData(
                            UserDTO.findFirst(
                                Neo4JRestService
                                        .getInstance()
                                        .postQuery(
                                                "MATCH (n:User) " +
                                                "WHERE ID(n) = %d " +
                                                "RETURN {id:id(n), labels: labels(n), data: n}",
                                                aLong
                                        )
                            )
                    )
                    .toModel();
        } catch (ConnectException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> queryForAll() {
        return null;
    }

    @Override
    public List<User> queryByField(String fieldName, Object value) throws ConnectException {
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
        Neo4JRestService.getInstance().postQuery(
                "CREATE (n:User { username: '%s', gitUsername: '%s', firstName: '%s', lastName: '%s', age: %d, mainJob: '%s', password: '%s', gcmRegId: '%s' }) ",
                data.getUsername(),
                data.getGitUsername(),
                data.getFirstName(),
                data.getLastName(),
                data.getAge(),
                data.getMainJob(), data.getPassword(), data.getGcmId());
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
