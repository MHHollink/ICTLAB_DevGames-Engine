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

        UserWithPassword[] users = {
            new UserWithPassword("Mjollnir","Mjollnir94","Marcel",null,"Hollink",22,"App developer", "Admin"),
            new UserWithPassword("Evestar","Evestar01","Evert-Jan",null,"Heilema",22,"Backend developer", "Admin"),
            new UserWithPassword("Walter","0000000","Wouter",null,"Naloop",22,"Backend developer", "Admin"),
            new UserWithPassword("Joris","Jorikito","Jorik",null,"Schoutengit ",22,"Backend developer", "Admin"),
            new UserWithPassword("Jelle","Draikos","Jelle","van","Tooren",22,"Security Engineer", "Admin")
        };
    }

}
