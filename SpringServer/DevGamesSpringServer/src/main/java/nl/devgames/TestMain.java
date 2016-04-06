package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMMessageComposer;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.connection.gcm.GCMRestService;
import nl.devgames.model.*;
import nl.devgames.score.SQReport;
import nl.devgames.utils.L;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestMain {

    static Project[] projects = {
            new Project("Clarity","AR app for the Port of Rotterdam."),
            new Project("Adventure Track", "Geolocation based Rol playing game."),
            new Project("DevGames","Programming gamificated to ensure you code better")
    };

    static UserWithPassword[] users = {
            new UserWithPassword("Marcel","Mjollnir94","Marcel",null,"Hollink",22,"App Developer", null, null ,null, "APA91bH9_5pjDv1TIDorZoRcm8Ye_bTGJK6eFTKQuVJI1lGf12EdSXfwmv1wtc9hXFj82eHb8O5b_ta7zxDlfbtCGiRdMxugCZ3N1e_WfPWSbZYBlaT15VpZuGxgKW_t77FrEM8jcIln", "admin"),
            new UserWithPassword("Evestar","Evestar01","Evert-Jan",null,"Heilema",22,"Backend developer", null, null, null, null, "admin"),
            new UserWithPassword("Joris","Jorikito","Jorik",null,"Schouten",22,"Backend developer", null, null, null, "ctUfXcOYkEw:APA91bHchgJZU-hMRxXuwxFbKQsIOKAl82HORl53EQZGzjPbdlccufAYPBwiO3d9-YkikxW41VitbwisaqJHjXmzTe5IIZD08PXYHVlIOzHtXOHyLu7E6x3qtHg6g7BHM_TDG7IrIYjy", "admin"),
    };

    static Business[] businesses = {
            new Business("DevGames", new HashSet<User>(Arrays.asList(users)){}, new HashSet<>(Arrays.asList(projects)))
    };

    public static void main(String[] args) {

//        fillDummyDB();
    	
//    	GCMMessageComposer.sendMessage(GCMMessageType.PLAIN_NOTIFICATION, "EY", "hallo daar");
    	
    	loadTestPush();

    }
    
    private static void loadTestPush() {
		try {
	         File file = new File("jsonJenkins.txt");
	         Scanner scanner = new Scanner(file);
	         String reportAsString = scanner.useDelimiter("\\Z").next();
	         scanner.close();
	         JsonObject reportAsJson = new JsonParser().parse(reportAsString).getAsJsonObject();
	         SQReport testReport = new SQReport();
	         testReport.buildFromJson(reportAsJson);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
                            "lastName: '%s', age: %d, mainJob: '%s', password: '%s'," +
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
