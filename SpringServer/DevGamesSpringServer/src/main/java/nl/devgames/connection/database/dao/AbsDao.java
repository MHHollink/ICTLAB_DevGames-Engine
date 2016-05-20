package nl.devgames.connection.database.dao;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.User;

import java.net.ConnectException;

/**
 * Created by Marcel on 19-5-2016.
 */
public abstract class AbsDao<T, ID> implements Dao<T, ID> {

    protected String createRelationship(long a, long b, User.Relations r) throws ConnectException {
        return Neo4JRestService.getInstance().postQuery(
                "MATCH (a:User), (b:Project) " +
                        "WHERE ID(a) = %d AND ID(b) = %d " +
                        "CREATE (a)-[:%s]->(b)",
                a, b, r.name()
        );
    }
}
