package nl.devgames.connection.database.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.BusinessDTO;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Business;
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

/**
 * Created by Jorikito on 18-May-16.
 */
public class BusinessDao implements Dao<Business, Long> {
    @Override
    public Business queryById(Long id) throws ConnectException, IndexOutOfBoundsException {
        BusinessDTO dto = null;
        Set<User> employees = new HashSet<>();
        Set<Project> projects = new HashSet<>();
        String response = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Business) " +
                        "WHERE ID(a) = %d " +
                        "OPTIONAL " +
                        "MATCH a-[]->(b) " +
                        "WHERE ID(a) = %d " +
                        "RETURN " +
                        "{id:id(a), labels: labels(a), data: a}," +
                        "{id:id(b), labels: labels(b), data: b}",
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
                String label = row.getAsJsonObject().get("labels").getAsJsonArray().get(0).getAsString();

                switch (label) {
                    case "Business" :
                        if(dto == null) dto = new BusinessDTO().createFromNeo4jData(row.getAsJsonObject());
                        else {
                            BusinessDTO bTemp = new BusinessDTO().createFromNeo4jData(row.getAsJsonObject());
                            if(!dto.equalsInContent(bTemp))
                                L.w("Two different DTO's were found in the response. 1:'%s', 2:'%s'", dto, bTemp);
                        }
                        break;
                    case "User" :
                        employees.add(new UserDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    case "Project" :
                        projects.add(new ProjectDTO().createFromNeo4jData(row.getAsJsonObject()).toModel());
                        break;
                    default:
                        L.w("Unimplemented case detected : '%s'", label);
                }
            }
        }
        if(dto == null) return null;

        dto.employees = employees;
        dto.projects = projects;

        return dto.toModel();
    }

    @Override
    public List<Business> queryForAll() throws ConnectException, IndexOutOfBoundsException {
        String r = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Business) RETURN {id:id(n), labels: labels(n), data: n}"
        );

        List<Business> response = new ArrayList<>();
        for (JsonObject object : BusinessDTO.findAll(r)) {
            response.add(new BusinessDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public List<Business> queryByField(String fieldName, Object value) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat;
        if(value instanceof Number)
            queryFormat = "MATCH (n:Business) WHERE n.%s =  %s  RETURN {id:id(n), labels: labels(n), data: n}";
        else
            queryFormat = "MATCH (n:Business) WHERE n.%s = '%s' RETURN {id:id(n), labels: labels(n), data: n}";

        String r = Neo4JRestService.getInstance().postQuery(
                queryFormat,
                fieldName,
                value
        );

        List<Business> response = new ArrayList<>();
        for (JsonObject object : BusinessDTO.findAll(r)) {
            response.add(
                    queryById(
                            new BusinessDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public List<Business> queryByFields(Map<String, Object> fieldValues) throws ConnectException, IndexOutOfBoundsException {
        String queryFormat = "MATCH (n:Business) WHERE ";

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

        List<Business> response = new ArrayList<>();
        for (JsonObject object : BusinessDTO.findAll(r)) {
            response.add(
                    queryById(
                            new BusinessDTO().createFromNeo4jData(object).toModel().getId()
                    )
            );
        }
        return response;
    }

    @Override
    public Business queryBySameId(Business business) throws ConnectException, IndexOutOfBoundsException {
        return queryById(business.getId());
    }

    public List<Business> queryFromProject(long id) throws ConnectException {
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Business)-[:has_project]->(b:Project) " +
                        "WHERE ID(b) = %d " +
                        "RETURN {id:id(a), labels: labels(a), data: a}",
                id
        );

        List<Business> response = new ArrayList<>();
        for (JsonObject object : BusinessDTO.findAll(responseString)) {
            response.add(new BusinessDTO().createFromNeo4jData(object).toModel());
        }
        return response;
    }

    @Override
    public int create(Business business) throws ConnectException, IndexOutOfBoundsException {
        String response = Neo4JRestService.getInstance().postQuery(
                "CREATE (n:Business { name: '%s' }) RETURN {id:id(n), labels: labels(n), data: n} ",
                business.getName()
        );

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if(json.get("errors").getAsJsonArray().size() != 0)
            L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
        return json.get("results").getAsJsonArray().size();
    }

    @Override
    public Business createIfNotExists(Business data) throws ConnectException, IndexOutOfBoundsException {
        Business business = data.getId() != null ? queryById(data.getId()) : null;
        if (business == null || !business.equals(data)) {
            int inserted = create(data);
            if (inserted == 0)
                return null;
            L.d("Created %d rows", inserted);
            return queryByField("name", data.getName()).get(0);
        } else return business;
    }

    @Override
    public int update(Business business) throws ConnectException, IndexOutOfBoundsException {
        if(business != null && queryById(business.getId()) != null) {

            String response = Neo4JRestService.getInstance().postQuery(
                    "MATCH (n:Business) " +
                            "WHERE ID(n) = %d " +
                            "SET n.name = '%s' " +
                            "RETURN {id:id(n), labels: labels(n), data: n} ",
                    business.getId(),
                    business.getName()
            );

            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            if(json.get("errors").getAsJsonArray().size() != 0)
                L.e("Errors were found during neo4j request : %s", json.get("errors").getAsJsonArray());
            return json.get("results").getAsJsonArray().size();
        }
        L.w("Business is null or has no id that is present in the database");
        return 0;
    }

    @Override
    public int delete(Business business) throws ConnectException, IndexOutOfBoundsException {
        return deleteById(business.getId());
    }

    @Override
    public int deleteById(Long aLong) throws ConnectException, IndexOutOfBoundsException {
        return 0;
    }

    @Override
    public int delete(Collection<Business> businesses) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Business business : businesses)
            changed += delete(business);
        return changed;
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws ConnectException, IndexOutOfBoundsException {
        int changed = 0;
        for(Long id : ids)
            changed += deleteById(id);
        return changed;
    }

    public int saveRelationship(Business business, User user) {
        return 0;
    }


    public int saveRelationship(Business business, Project project) {
        return 0;
    }
}
