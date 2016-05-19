package nl.devgames.rest.controller;

import com.google.gson.JsonObject;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.Dao;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.connection.database.dto.CommitDTO;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorikito on 18-May-16.
 */
public class BusinessDao implements Dao<Business, Long> {
    @Override
    public Business queryById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Business> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Business> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<Business> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Business queryBySameId(Business data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    public List<Business> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Business)-[:has_project]->(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN a",
                id
        );

        List<Business> response = new ArrayList<>();
        for (JsonObject object : BusinessDTO.findAll(responseString)) {
            response.add(new BusinessDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Business data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public Business createIfNotExists(Business data) throws ConnectException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int update(Business data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Business data) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Business> datas) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int deleteIds(Collection<Long> longs) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }
}
