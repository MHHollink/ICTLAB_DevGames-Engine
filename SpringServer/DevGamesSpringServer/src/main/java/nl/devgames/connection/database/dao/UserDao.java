package nl.devgames.connection.database.dao;

import com.google.gson.JsonObject;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import javax.jws.soap.SOAPBinding;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcel on 12-5-2016.
 */
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
    public List<User> queryForField(String fieldName, Object value) {
        try {
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
        catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exeption, Database might be offline!");
        }
        return null;
    }

    @Override
    public List<User> queryForFieldValues(Map<String, Object> fieldValues) {
        return null;
    }

    @Override
    public User queryForSameId(User data) {
        return null;
    }

    @Override
    public int create(User data) {
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
