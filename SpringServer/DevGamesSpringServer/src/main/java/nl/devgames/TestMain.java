package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.connection.gcm.GCMRestService;
import nl.devgames.model.*;
import nl.devgames.utils.L;

import java.util.Arrays;
import java.util.HashSet;

public class TestMain {

    static UserWithPassword[] users = {
            new UserWithPassword("Marcel","Mjollnir94","Marcel",null,"Hollink",22,"App Developer", null, null ,null, null, "admin"),
            new UserWithPassword("Evestar","Evestar01","Evert-Jan",null,"Heilema",22,"Backend developer", null, null, null, null, "admin"),
            new UserWithPassword("Joris","Jorikito","Jorik",null,"Schouten",22,"Backend developer", null, null, null, null, "admin"),
    };

    static Project[] projects = {
            new Project("Clarity","AR app for the Port of Rotterdam."),
            new Project("Adventure Track", "Geolocation based Rol playing game."),
            new Project("DevGames","Programming gamificated to ensure you code better")
    };

    static Business[] businesses = {
            new Business("DevGames", new HashSet<User>(Arrays.asList(users)){}, new HashSet<>(Arrays.asList(projects)))
    };

    public static void main(String[] args) {

        fillDummyDB();

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

    private static void fillDummyDB(){
        Neo4JRestService.getInstance().postQuery("MATCH n DETACH DELETE n");

        addDummyUsers();
        addDummyProjects();
        addDummyBusiness();
        addDummyPushes();
        addDummyCommits();
        addDummyIssues();
        addDummyDuplications();

        addRelationships();
    }

    private static void addDummyPushes() {
    }

    private static void addDummyCommits() {

    }

    private static void addDummyIssues() {

    }

    private static void addDummyDuplications() {

    }

    private static void addDummyBusiness() {
        for (Business business : businesses) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Business { " +
                            "name: '%s' })",
                    business.getName()
            );
        }
    }

    private static void addDummyProjects() {
        for (Project project : projects) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Project { " +
                            "name: '%s', description: '%s' })",
                    project.getName(),
                    project.getDescription()
            );
        }
    }

    private static void addDummyUsers() {
        for (UserWithPassword user : users) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:User { " +
                            "username: '%s', gitUsername: '%s', firstName: '%s', " +
                            "lastName: '%s', age: %d, job: '%s', password: '%s'," +
                            "gcmRegId: '%s' }) ",
                    user.getUsername(),
                    user.getGitUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getMainJob(),
                    user.getPassword(),
                    user.getGcmId()
            );
        }
    }

    private static void addRelationships(){

    }

}
