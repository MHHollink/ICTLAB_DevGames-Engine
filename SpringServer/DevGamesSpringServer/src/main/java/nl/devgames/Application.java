package nl.devgames;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String SESSION_HEADER_KEY = "SESSION_ID";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private Application() {}

}
