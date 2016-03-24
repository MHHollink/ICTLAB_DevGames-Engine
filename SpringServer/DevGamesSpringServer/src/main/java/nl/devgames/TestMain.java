package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;

public class TestMain {

    public static void main(String[] args) {

        Neo4JRestService.getInstance().postQuery(
                "match (n:User) where n.username = 'Mjollnir94' return n"
        );
    }
}
