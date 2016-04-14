package nl.devgames.connection.database;


import nl.devgames.connection.AbsRestService;
import nl.devgames.utils.L;
import nl.devgames.utils.Tuple;

import java.io.IOException;

public class Neo4JRestService extends AbsRestService {

    private static Neo4JRestService instance;

    public static Neo4JRestService getInstance() {
        if (instance == null) {
            instance = new Neo4JRestService();
        }
        return instance;
    }

    private Neo4JRestService() {
        super("http://localhost:7474/db/data/transaction/commit");
    }


    /**
     * Post a json object to the {@link #url}
     *
     * @param json string object to send
     * @return response string
     */
    public String post(String json) {
        try {
            super.post(json, new Tuple<>("Authorization","Basic bmVvNGo6ZGV2Z2FtZXM="));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        L.og("Recieved response: %s", response);
        return response;
    }

    /**
     * formats a Query to the object the neo4j rest api wants
     *
     * @param query plain query with parameters
     * @return a response string
     *
     * EXAMPLE QUERY :
     *
     *  {
     *      "statements": [
     *          {
     *              "statement":"MATCH n DETACH DELETE n"
     *          }
     *       ]
     *  }
     */
    private String queryToJson(String query) {
        return "{\"statements\":[{\"statement\":\""+query+"\"}]}";
    }

    /**
     * Post a chipher query to the neo4j database via rest.
     *
     * @param query query with empty parameter fields as " MATCH n WHERE ID = %d RETURN N"
     * @param params all parameters used in the query
     * @return Response String.
     */
    public String postQuery(String query, Object... params) {
        query = String.format(query,params);
        L.og(query);

        return post(
                queryToJson(
                        query
                )
        );
    }

}
