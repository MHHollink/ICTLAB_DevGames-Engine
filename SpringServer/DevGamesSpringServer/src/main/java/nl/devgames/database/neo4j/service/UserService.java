package nl.devgames.database.neo4j.service;

import nl.devgames.database.neo4j.factory.Neo4JSessionFactory;
import nl.devgames.model.User;

import java.util.Collections;
import java.util.Map;

public class UserService extends GenericService<User> implements UserServiceIntf {

    @Override
    public Iterable<Map<String,Object>> getOwnUser() {
        String query =
                "MATCH n WHERE n.gitUsername = \"Mjollnir94\" RETURN n";

        return Neo4JSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
    }


    @Override
    public Class<User> getModelType() {
        return User.class;
    }
}
