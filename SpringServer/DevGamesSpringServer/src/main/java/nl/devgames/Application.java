package nl.devgames;

import org.neo4j.ogm.service.Components;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String GCM_API_KEY = "";
    public static final String SESSION_HEADER_KEY = "HEADER_ID";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .setURI("http://user:password@localhost:7474");
    }

}
