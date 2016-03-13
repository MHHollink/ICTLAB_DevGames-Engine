package gcm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GCMMessage implements Serializable{
	private List<String> registration_ids;
	private Map<String,String> notification;

    public void addRegToken(String regToken){
        if(registration_ids == null)
        	registration_ids = new LinkedList<String>();
        registration_ids.add(regToken);
    }

    public void createData(String title, String message){
        if(notification == null)
        	notification = new HashMap<String,String>();

        notification.put("title", title);
        notification.put("text", message);
    }
    
    public List<String> getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}

	public Map<String, String> getNotification() {
		return notification;
	}

	public void setNotification(Map<String, String> notification) {
		this.notification = notification;
	}

}
