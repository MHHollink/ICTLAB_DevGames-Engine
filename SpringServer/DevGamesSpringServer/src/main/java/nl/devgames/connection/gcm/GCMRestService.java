package nl.devgames.connection.gcm;

import nl.devgames.connection.AbsRestService;
import nl.devgames.connection.RequestProperty;

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
                    new RequestProperty("Authorization", API_TOKEN)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
