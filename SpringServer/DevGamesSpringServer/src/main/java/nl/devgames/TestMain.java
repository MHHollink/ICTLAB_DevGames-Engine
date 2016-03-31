package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.connection.gcm.GCMRestService;
import nl.devgames.model.User;
import nl.devgames.model.UserWithPassword;
import nl.devgames.utils.L;

public class TestMain {

    public static void main(String[] args) {
        Neo4JRestService service = Neo4JRestService.getInstance();

        service.postQuery("MATCH n DETACH DELETE n");

        UserWithPassword[] users = {
            new UserWithPassword("Mjollnir","Mjollnir94","Marcel",null,"Hollink",22,"App developer", "Admin"),
            new UserWithPassword("Evestar","Evestar01","Evert-Jan",null,"Heilema",22,"Backend developer", "Admin"),
            new UserWithPassword("Walter","0000000","Wouter",null,"Naloop",22,"Backend developer", "Admin"),
            new UserWithPassword("Joris","Jorikito","Jorik",null,"Schoutengit ",22,"Backend developer", "Admin"),
            new UserWithPassword("Jelle","Draikos","Jelle","van","Tooren",22,"Security Engineer", "Admin")
        };

        for (UserWithPassword user : users) {
            service.postQuery(
                    "CREATE (n:User { username: '%s', gitUsername: '%s', firstName: '%s', lastName: '%s', age: %d, job: '%s', password: '%s' }) ",
                    user.getUsername(),
                    user.getGitUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getMainJob(),
                    user.getPassword()
            );
        }

//        GCMMessage message = new GCMMessage();
//        message.addToken("GCM_TOKEN");
//        message.createNotification(
//                GCMMessageType.PLAIN_NOTIFICATION,
//                "GCM_MESSAGE_TITLE",
//                "GCM_MESSAGE"
//        );
//
//        GCMRestService.getInstance().postMessage(
//                message
//        );
    }

}
