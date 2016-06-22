package nl.devgames.connection.gcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.AbsRestService;
import nl.devgames.utils.L;
import nl.devgames.utils.Tuple;

import java.io.IOException;

public class GCMRestService extends AbsRestService {

    //devgames project
//    private static final String API_TOKEN = "AIzaSyC9G357o3fykQPsbANNtn6KXOkHJi6_kwA";

    //joriks devgames project
    private static final String API_TOKEN = "AIzaSyAW3nAfcMKPD_WWI42Tzb8p0dG-Jh8Ybx8";
	
	//test project
//	private static final String API_TOKEN = "AIzaSyAYYJpgR6yk4AePQioLiMKVNsOVyMrcKVU";

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

    /**
     * Posts a JSON object as string to the {@link GCMRestService#url}
     * @param json string to post
     */
    public String post(String json) {
        try {
            super.post(json,
                    new Tuple<>(
                            "Authorization", "key="+API_TOKEN
                    )
            );
        } catch (IOException e) {
            L.e(e, "Failure in reading response");
            return null;
        }
        L.d("Recieved response: %s", response);

        JsonObject r = new JsonParser().parse(response).getAsJsonObject();
        L.i("GCM-Message send! Success:%d, Failure:%d",
                r.get("success").getAsInt(),
                r.get("failure").getAsInt()
        );
        return response;
    }

    /**
     * Converts a {@link GCMMessage} to a json.
     *
     * @param message message which will be converted
     * @return json object for GCM push notification
     *
     * Example JSON :
     *
     *  {
     *      "registration_ids" : [ "GCM_TOKEN" ],
     *      "data" : {
     *          "text" : "GCM_MESSAGE",
     *          "type" : "PLAIN_NOTIFICATION",
     *          "title" : "GCM_MESSAGE_TITLE"
     *      }
     *  }
     */
    private String messageToJson(GCMMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        } catch (JsonProcessingException e) {
            L.e(e, "Writing message to Json");
        }
        return null;
    }

    /**
     * Post A Message as {@link GCMMessage} which will be
     * converted via {@link #messageToJson(GCMMessage)}
     * and send via {@link #post(String)}
     *
     * @param message Message object filled with data en tokens.
     */
    public String postMessage(GCMMessage message) {
        L.d(message.toString());
        post(
                messageToJson(
                        message
                )
        );
        return response;
    }
}
