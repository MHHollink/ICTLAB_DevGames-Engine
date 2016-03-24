package nl.devgames.connection.gcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.devgames.connection.AbsRestService;
import nl.devgames.connection.Tuple;

import java.io.IOException;

public class GCMRestService extends AbsRestService {

    private static final String API_TOKEN = "AIzaSyAYYJpgR6yk4AePQioLiMKVNsOVyMrcKVU";

    private static GCMRestService instance;

    public static GCMRestService getInstance() {
        if (instance == null) {
            instance = new GCMRestService();
        }
        return instance;
    }

    private GCMRestService() {
        super("https://gcm-http.googleapis.com/gcm/send");
    }

    public void post(String json) {
        try {
            super.post(json,
                    new Tuple<>("Authorization", API_TOKEN)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String messageToJson(GCMMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void postMessage(GCMMessage message) {
        post(
                messageToJson(
                        message
                )
        );
    }
}
