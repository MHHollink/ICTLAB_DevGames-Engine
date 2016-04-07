package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMMessageComposer;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.connection.gcm.GCMRestService;
import nl.devgames.model.*;
import nl.devgames.rest.controller.ProjectController;
import nl.devgames.score.SQReport;
import nl.devgames.utils.L;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    static Commit[] commits = {
            new Commit("b699883e3ccf7afbed8573d5c8add56e12f8393e", "Added .gitattributes & .gitignore files", 1455217086),
            new Commit("58c38eb08dce96f734644a0aa17c8ff8939b531e", "Fixed SDK versions in Android gradle", 1455994686)
    };

    static Push[] pushes = {
            new Push(projects[2], new HashSet<>(Arrays.asList(commits)), new HashSet<>(), new HashSet<>(), 1455994686)
    };

    static Duplication[] duplications = {
            new Duplication(new HashSet<DuplicationFile>(){{
                add(new DuplicationFile("filename", 10,17,7));
                add(new DuplicationFile("filename", 10,17,7));
                add(new DuplicationFile("filename", 10,17,7));
            }}),
            new Duplication(new HashSet<DuplicationFile>(){{
                add(new DuplicationFile("filename", 10,17,7));
                add(new DuplicationFile("filename", 10,17,7));
                add(new DuplicationFile("filename", 10,17,7));
            }}),
            new Duplication(new HashSet<DuplicationFile>(){{
                add(new DuplicationFile("filename", 10,17,7));
                add(new DuplicationFile("filename", 10,17,7));
                add(new DuplicationFile("filename", 10,17,7));
            }})
    };

    static Issue[] issues = {
            new Issue("MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null, "This application is still shit", 840, 1455217086, 1459624317, 0)
    };

    public static void main(String[] args) {

//        fillDummyDB();

//    	  GCMMessageComposer.sendMessage(GCMMessageType.PLAIN_NOTIFICATION, "EY", "hallo daar");

    	loadTestPush();

//        List<String> tokens = new ProjectController().getProjectMembersTokens("Marcel", "DevGames");
//
//        GCMMessage gcmMessage = new GCMMessage();
//
//        gcmMessage.addToken(tokens);
//        gcmMessage.createNotification(
//                GCMMessageType.PLAIN_NOTIFICATION,
//                "Plain test message",
//                "this is a test message for the neo4j query"
//        );
//
//        GCMRestService.getInstance().postMessage(gcmMessage);

        //fillDummyDB();

    }

    private static void loadTestPush() {
		try {
	         File file = new File("jsonJenkins.txt");
	         Scanner scanner = new Scanner(file);
	         String reportAsString = scanner.useDelimiter("\\Z").next();
	         scanner.close();
	         JsonObject reportAsJson = new JsonParser().parse(reportAsString).getAsJsonObject();
	         SQReport testReport = new SQReport().buildFromJson(reportAsJson);
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
        for (Push push : pushes) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Push { " +
                            "timestamp: '%s' })",
                    push.getTimestamp()
            );
        }
    }

    private static void addDummyCommits() {
        for (Commit commit : commits) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Commit { " +
                            "commitId: '%s', commitMsg: '%s', timestamp: %d })",
                    commit.getCommitId(),
                    commit.getCommitMsg(),
                    commit.getTimeStamp()
            );
        }
    }

    private static void addDummyIssues() {
        for (Issue issue : issues) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Issue { " +
                            "severity: '%s', component: '%s', message: '%s', " +
                            "status: '%s', resolution: '%s', dept: %d, " +
                            "startLine: %d, endLine: %d, creationDate : %d," +
                            "updateData: %d, closeData: %d })",
                    issue.getSeverity(),
                    issue.getComponent(),
                    issue.getMessage(),
                    issue.getStatus(),
                    issue.getResolution(),
                    issue.getDebt(),
                    issue.getStartLine(),
                    issue.getEndLine(),
                    issue.getCreationDate(),
                    issue.getUpdateDate(),
                    issue.getCloseDate()
            );
        }
    }

    private static void addDummyDuplications() {
        for (Duplication duplication : duplications) {
            Neo4JRestService.getInstance().postQuery(
                    "CREATE (n:Duplication)"
            );
        }
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
        Neo4JRestService s = Neo4JRestService.getInstance();

        s.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
        s.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
        s.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Adventure Track' }) CREATE (a)-[:is_developing]->(b)");

        s.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
        s.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
        s.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'Adventure Track' }) CREATE (a)-[:is_developing]->(b)");

        s.postQuery("MATCH (a:User { username: 'Joris' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
        s.postQuery("MATCH (a:User { username: 'Joris' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");

        s.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Marcel' }) CREATE (a)-[:has_employee]->(b)");
        s.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Evestar' }) CREATE (a)-[:has_employee]->(b)");
        s.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Joris' }) CREATE (a)-[:has_employee]->(b)");

        s.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Push { timestamp: '1455994686' }) CREATE (a)-[:pushed]->(b)");

        s.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Commit { commitId: 'b699883e3ccf7afbed8573d5c8add56e12f8393e' }) CREATE (a)-[:contains_commit]->(b)");
        s.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Commit { commitId: '58c38eb08dce96f734644a0aa17c8ff8939b531e' }) CREATE (a)-[:contains_commit]->(b)");

        s.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Issue { creationDate: 1455217086 }) CREATE (a)-[:has_issue]->(b)");

        s.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Duplication) CREATE (a)-[:has_duplication]->(b)");

        s.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:has_project]->(b)");



    }

}
