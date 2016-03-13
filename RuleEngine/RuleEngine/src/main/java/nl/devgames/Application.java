package nl.devgames;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import gcm.GCMMessage;
import gcm.GCMPoster;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
    }
    
    public static void sendTestMessage() {
    	System.out.println( "Sending POST to GCM" );

        String apiKey = "AIzaSyAYYJpgR6yk4AePQioLiMKVNsOVyMrcKVU";
        
        List<String> tokenList = new ArrayList<>();
        
        //joriks tel
        tokenList.add("ctUfXcOYkEw:APA91bHchgJZU-hMRxXuwxFbKQsIOKAl82HORl53EQZGzjPbdlccufAYPBwiO3d9-YkikxW41VitbwisaqJHjXmzTe5IIZD08PXYHVlIOzHtXOHyLu7E6x3qtHg6g7BHM_TDG7IrIYjy");
        
        String messageTitle = "Titel hallo";
        String messageText = "whatsup bra";
        GCMMessage message = createGCMMessage(messageTitle, messageText, tokenList);

        GCMPoster.post(apiKey, message);
    }

    public static GCMMessage createGCMMessage(String title, String text, List<String> tokenList){

    	GCMMessage message = new GCMMessage();

        message.createData(title, text);
        
        //add receivers
        for(String token : tokenList) {
        	message.addRegToken(token);
        }
        
        return message;
    }
}
