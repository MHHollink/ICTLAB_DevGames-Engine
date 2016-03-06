package nl.devgames.services.interfaces;

import nl.devgames.entities.Commit;
import nl.devgames.entities.Project;
import nl.devgames.entities.User;

import java.util.List;

/**
 * Created by Wouter on 1/9/2016.
 */
public interface UserService {
    public boolean validateUser(String username,String password);

    public List<Project> findAllProjectsOfUser(User user);
    public List<Commit> findAllCommitsOfUser(User user);

}
