package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String GCM_API_KEY = "";
    public static final String SESSION_HEADER_KEY = "HEADER_ID";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


    }

}
