package nl.devgames.connection.gcm;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class GCMMessage{

    private List<String> tokens;
    private Map<String,String> data;

    public GCMMessage() {
        tokens  = new ArrayList<>();
        data    = new HashMap<>();
    }

    public void createNotification(GCMMessageType type, String title, String message){
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
        Collections.addAll(tokens, token);
    }

    public List<String> getTokens() {
        return tokens;
    }

    public Map<String, String> getData() {
        return data;
    }

}