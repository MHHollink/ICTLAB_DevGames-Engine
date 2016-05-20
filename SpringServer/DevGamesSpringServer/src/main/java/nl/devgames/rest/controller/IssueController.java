package nl.devgames.rest.controller;

import nl.devgames.Application;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.database.dto.IssueDTO;
import nl.devgames.connection.database.dto.ProjectDTO;
import nl.devgames.connection.database.dto.PushDTO;
import nl.devgames.connection.database.dto.UserDTO;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.rest.errors.InvalidSessionException;
import nl.devgames.rest.errors.NotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;

@RestController
@RequestMapping(value = "/issues")
public class IssueController extends BaseController{

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Issue getIssueById(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                                   @PathVariable long id) throws ConnectException {
        Issue returnIssue = new Issue();

        //check if session valid
        String sessionResponseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User) " +
                        "WHERE n.sessionId = '%s' " +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                session
        );
        if (!new UserDTO().createFromNeo4jData(UserDTO.findFirst(sessionResponseString)).isValid())
            //session invalid
            throw new InvalidSessionException("Request session is not found");


        //get issue by id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Issue) " +
                        "WHERE n.id = '%d' " +
                        "RETURN {id:id(n), labels: labels(n), data: n}",
                id
        );

        IssueDTO issueDTO = new IssueDTO().createFromNeo4jData(
                IssueDTO.findFirst(responseString)
        );
        //return issue if valid
        if(issueDTO.isValid()) {
            returnIssue = issueDTO.toModel();
        }
        else {
            throw new NotFoundException("Request session is not found");
        }

        return returnIssue;
    }

    @RequestMapping(value = "{id}/pushes", method = RequestMethod.GET)
    public Push getPush(@RequestHeader(value = Application.SESSION_HEADER_KEY, required = false) String session,
                        @PathVariable long id) throws ConnectException {
        Push returnPush = new Push();

        //get push by issue id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Push)-[:has_issues]->(m:Issue { id:'%d' })" +
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

        //get user by issue id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:User)-[:pushed_by]->(p:Push)-[:has_issues]->(i:Issue { id:'%d' })" +
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

        //get project by issue id
        String responseString = Neo4JRestService.getInstance().postQuery(
                "MATCH (n:Project)<-[:pushed_to]-(p:Push)-[:has_issues]->(i:Issue { id:'%d' })" +
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
