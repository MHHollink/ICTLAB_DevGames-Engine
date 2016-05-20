package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.CommitDTO;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.connection.database.dto.PushDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Commit;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.InvalidSessionException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;

@RestController
@RequestMapping(value = "/commits")
public class CommitController extends BaseController{

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Commit getCommitById(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                @PathVariable long id) throws ConnectException {
        Commit returnCommit = new Commit();

        //get commit by id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Commit) " +
                        "WHERE n.id = '%d' " +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        CommitDTO commitDTO = new CommitDTO().createFromNeo4jData(
                CommitDTO.findFirst(responseString)
        );
        //return commit if valid
        if(commitDTO.isValid()) {
            returnCommit = commitDTO.toModel();
        }
        else {
            throw new InvalidSessionException("Request session is not found");
        }

        return returnCommit;
    }

    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Push getPush(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id) throws ConnectException {
        Push returnPush = new Push();

        //get push by commit id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Push)-[:contains_commits]->(m:Commit { id:'%d' })" +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        PushDTO pushDTO = new PushDTO().createFromNeo4jData(
                PushDTO.findFirst(responseString)
        );
        //return push if valid
        if(pushDTO.isValid()) {
            returnPush = pushDTO.toModel();
        }
        else {
            throw new InvalidSessionException("Request session is not found");
        }

        return returnPush;
    }

    @RequestMapping(value = "{id}/user", method = RequestMethod.GET)
    public User getUser(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id) throws ConnectException {
        User returnUser = new User();

        //get user by commit id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User)-[:pushed_by]->(p:Push)-[:contains_commits]->(i:Commit { id:'%d' })" +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        UserDTO userDTO = new UserDTO().createFromNeo4jData(
                UserDTO.findFirst(responseString)
        );
        //return user if valid
        if(userDTO.isValid()) {
            returnUser = userDTO.toModel();
        }
        else {
            throw new InvalidSessionException("Request session is not found");
        }

        return returnUser;
    }

    @RequestMapping(value = "{id}/projects", method = RequestMethod.GET)
    public Project getProject(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                              @PathVariable long id) throws ConnectException {
        Project returnProject = new Project();

        //get project by commit id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Project)<-[:pushed_to]-(p:Push)-[:contains_commits]->(i:Commit { id:'%d' })" +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        ProjectDTO projectDTO = new ProjectDTO().createFromNeo4jData(
                ProjectDTO.findFirst(responseString)
        );
        //return project if valid
        if(projectDTO.isValid()) {
            returnProject = projectDTO.toModel();
        }
        else {
            throw new InvalidSessionException("Request session is not found");
        }

        return returnProject;
    }
}
