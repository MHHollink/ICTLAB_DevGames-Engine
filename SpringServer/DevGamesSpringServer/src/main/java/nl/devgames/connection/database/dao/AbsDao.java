package nl.devgames.connection.database.dao;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.Business;
import nl.devgames.model.Duplication;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;

import java.net.ConnectException;

/**
 * Class to implement a method that creates any relation!k
 */
public abstract class AbsDao<T, ID> implements Dao<T, ID> {

    protected String createRelationship(long a, long b, User.Relations r) throws ConnectException {
        return Neo4JRestService.getInstance().postQuery(
                "MATCH (a:User), (b) " +
                        "WHERE ID(a) = %d AND ID(b) = %d " +
                        "CREATE (a)-[:%s]->(b)",
                a, b, r.name()
        );
    }

    protected String createRelationship(long a, long b, Business.Relations r) throws ConnectException {
        return Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Business), (b) " +
                        "WHERE ID(a) = %d AND ID(b) = %d " +
                        "CREATE (a)-[:%s]->(b)",
                a, b, r.name()
        );
    }

    protected String createRelationship(long a, long b, Push.Relations r) throws ConnectException {
        return Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Push), (b) " +
                        "WHERE ID(a) = %d AND ID(b) = %d " +
                        "CREATE (a)-[:%s]->(b)",
                a, b, r.name()
        );
    }

    protected String createRelationship(long a, long b, Duplication.Relations r) throws ConnectException {
        return Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Duplication), (b) " +
                        "WHERE ID(a) = %d AND ID(b) = %d " +
                        "CREATE (a)-[:%s]->(b)",
                a, b, r.name()
        );
    }

    protected String createRelationship(long a, long b, Project.Relations r) throws ConnectException {
        return Neo4JRestService.getInstance().postQuery(
                "MATCH (a:Project), (b) " +
                        "WHERE ID(a) = %d AND ID(b) = %d " +
                        "CREATE (a)-[:%s]->(b)",
                a, b, r.name()
        );
    }
}
