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

    /**
     * Fill in the data for the push notification
     *
     * @param type     {@link GCMMessageType} of the message
     * @param title     Title of the message
     * @param message   Message it self
     */
    public void createNotification(GCMMessageType type, String title, String message){
        L.d("Created Notification");
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

    /**
     * Adds a list of tokens (or one) to the push notification
     *
     * @param token String value of the users GCM token
     */
    public void addToken(String... token){
        Collections.addAll(registration_ids, token);
    }

    public void addToken(List<String> tokens) {
        registration_ids.addAll(tokens);
    }

    public void addToken(Set<String> tokens) {
        registration_ids.addAll(tokens);
    }

    /**
     * Getter for all id's used to map in {@link GCMRestService#messageToJson(GCMMessage)}
     *
     * @return array of all tokens for receives for this notification
     */
    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    /**
     * Getter for the data used to map in {@link GCMRestService#messageToJson(GCMMessage)}
     *
     * @return array of all tokens for receives for this notification
     */
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "GCMMessage{" +
                "registration_ids=" + registration_ids +
                ", data=" + data +
                '}';
    }
}