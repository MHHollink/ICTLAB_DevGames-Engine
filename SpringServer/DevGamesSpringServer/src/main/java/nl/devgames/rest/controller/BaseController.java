package nl.devgames.rest.controller;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dao.CommitDao;
import nl.devgames.connection.database.dao.DuplicationDao;
import nl.devgames.connection.database.dao.IssueDao;
import nl.devgames.connection.database.dao.ProjectDao;
import nl.devgames.connection.database.dao.PushDao;
import nl.devgames.connection.database.dao.UserDao;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.connection.gcm.GCMRestService;
import nl.devgames.model.Business;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * BaseController contains every method that all other controllers should have access to.
 */
@RestController
public abstract class BaseController {

    protected User getUserFromSession(String session) {
        if (session == null || session.isEmpty())
            throw new BadRequestException("Request without session"); // throws exception when session is null or blank
        try {
            return new UserDao().queryByField("session", session).get(0);
        } catch (ConnectException e) {
            L.e("Database service is offline!");
            throw new KnownInternalServerError("Database service offline!");
        } catch (IndexOutOfBoundsException e) {
            L.w("User was not found");
            throw new InvalidSessionException("Session invalid!");
        }
    }




    @RequestMapping(value = "/test/insert", method = RequestMethod.POST)
    public boolean setUpDb() throws ConnectException {

        Project clarity = new Project("Clarity","AR app for the Port of Rotterdam."),
                adventure = new Project("Adventure Track", "Geolocation based Rol playing game."),
                devGames = new Project("DevGames","Programming gamificated to ensure you code better");

        User marcel = new User("Marcel","Mjollnir94","Marcel",null,"Hollink", "admin"),
                evert = new User("Evestar","Evestar01","Evert-Jan",null,"Heilema", "admin"),
                jorik = new User("Joris","Jorikito","Jorik",null,"Schouten", "admin");

        Business dg = new Business("DevGames");

        Commit a = new Commit("b699883e3ccf7afbed8573d5c8add56e12f8393e", "Added .gitattributes & .gitignore files", 1455217086),
                b = new Commit("58c38eb08dce96f734644a0aa17c8ff8939b531e", "Fixed SDK versions in Android gradle", 1455994686);

        Push c = new Push(UUID.randomUUID().toString(), 1455994686);

        Duplication duplication =
                new Duplication(
                        new HashSet<DuplicationFile>(){
                            {
                            add(new DuplicationFile("filename", 10,17,7));
                            add(new DuplicationFile("filename", 10,17,7));
                            add(new DuplicationFile("filename", 10,17,7));
                            }
                        }
                );

        Issue issue =
                new Issue(
                        213456, "MAJOR", "nl.devgames.Application", 11, 13, "OPEN", null,
                        "This application is still shit", 840, 1455217086, 1459624317, 0
                );


        Neo4JRestService dbService = Neo4JRestService.getInstance();
        dbService.postQuery("MATCH n DETACH DELETE n");

        UserDao userDao = new UserDao();
        CommitDao commitDao = new CommitDao();
        ProjectDao projectDao = new ProjectDao();
        IssueDao issueDao = new IssueDao();
        DuplicationDao duplicationDao = new DuplicationDao();
        BusinessDao businessDao = new BusinessDao();
        PushDao pushDao = new PushDao();

        commitDao.createIfNotExists(a);
        commitDao.createIfNotExists(b);
        pushDao.createIfNotExists(c);
        issueDao.createIfNotExists(issue);
        duplicationDao.createIfNotExists(duplication);
        businessDao.createIfNotExists(dg);

        adventure = projectDao.createIfNotExists(adventure);
        devGames = projectDao.createIfNotExists(devGames);
        clarity = projectDao.createIfNotExists(clarity);

        marcel = userDao.createIfNotExists(marcel);
        evert = userDao.createIfNotExists(evert);
        jorik = userDao.createIfNotExists(jorik);

        userDao.saveRelationship(marcel, devGames);
        userDao.saveRelationship(marcel, clarity);
        userDao.saveRelationship(marcel, adventure);
        userDao.saveRelationship(evert, devGames);
        userDao.saveRelationship(evert, clarity);
        userDao.saveRelationship(evert, adventure);
        userDao.saveRelationship(jorik, devGames);
        userDao.saveRelationship(jorik, clarity);

        userDao.saveRelationship(marcel, c);




//        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Adventure Track' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'Adventure Track' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Joris' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Joris' }), (b:Project { name: 'Clarity' }) CREATE (a)-[:is_developing]->(b)");
//        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Marcel' }) CREATE (a)-[:has_employee]->(b)");
//        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Evestar' }) CREATE (a)-[:has_employee]->(b)");
//        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:User { username: 'Joris' }) CREATE (a)-[:has_employee]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Push { timestamp: '1455994686' }) CREATE (a)-[:pushed]->(b)");
//        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Commit { commitId: 'b699883e3ccf7afbed8573d5c8add56e12f8393e' }) CREATE (a)-[:contains_commit]->(b)");
//        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Commit { commitId: '58c38eb08dce96f734644a0aa17c8ff8939b531e' }) CREATE (a)-[:contains_commit]->(b)");
//        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Issue { creationDate: 1455217086 }) CREATE (a)-[:has_issue]->(b)");
//        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Duplication) CREATE (a)-[:has_duplication]->(b)");
//        dbService.postQuery("MATCH (a:Business { name: 'DevGames' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:has_project]->(b)");
//        dbService.postQuery("MATCH (a:Push { timestamp: '1455994686' }), (b:Project { name: 'DevGames' }) CREATE (a)-[:pushed_to]->(b)");
//        dbService.postQuery("MATCH (a:User { username: 'Marcel' }), (b:Project { name: 'DevGames' }) CREATE (b)-[:is_lead_by]->(a)");
//        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Clarity' }) CREATE (b)-[:is_lead_by]->(a)");
//        dbService.postQuery("MATCH (a:User { username: 'Evestar' }), (b:Project { name: 'Adventure Track' }) CREATE (b)-[:is_lead_by]->(a)");

        return true;
    }

    @RequestMapping(value = "/test/push", method = RequestMethod.POST)
    public String gcmTestNotification() throws ConnectException {
        UserDao dao = new UserDao();
        List<User> users = dao.queryForAll();
        GCMMessage message = new GCMMessage();
        message.createNotification(
                GCMMessageType.NEW_PUSH_RECEIVED,
                "",
                String.valueOf(400)
        );
        users.parallelStream().filter(user -> user.getGcmId() != null).forEach( u -> message.addToken(u.getGcmId()) );
        return GCMRestService.getInstance().postMessage(message);
    }

}
