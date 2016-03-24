package nl.devgames.connection.database;


import nl.devgames.connection.AbsRestService;
import nl.devgames.connection.Tuple;

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

    public void post(String json) {
        try {
            super.post(json,
                    new Tuple<>("Authorization","Basic bmVvNGo6OTVkMGU3NjIxNzlj")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String queryToJson(String query) {
        return "{\"statements\":[{\"statement\":\""+query+"\"}]}";
    }

    public void postQuery(String query) {
        post(
                queryToJson(
                        query
                )
        );
    }

}
