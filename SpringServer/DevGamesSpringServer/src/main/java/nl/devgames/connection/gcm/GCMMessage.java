package nl.devgames.connection.gcm;

import nl.devgames.utils.L;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class GCMMessage{

    private List<String> registration_ids;
    private Map<String,String> data;

    public GCMMessage() {
        registration_ids  = new ArrayList<>();
        data    = new HashMap<>();
    }

    public void createNotification(GCMMessageType type, String title, String message){
        L.og("Created Notification");
        switch(type) {
            case PLAIN_NOTIFICATION:
            case NEW_DEVICE_REGISTERED:
            case NEW_SCORES:
                data.put("type", type.toString());
                data.put("title", title);
                data.put("text", message);
                break;
            default:
                throw new NotImplementedException();
        }
    }

    public void addToken(String... token){
        Collections.addAll(registration_ids, token);
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public Map<String, String> getData() {
        return data;
    }
}