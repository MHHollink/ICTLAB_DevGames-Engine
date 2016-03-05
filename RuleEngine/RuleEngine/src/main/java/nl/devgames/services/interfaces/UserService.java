package nl.devgames.services.interfaces;

import devgames.entities.Commit;
import devgames.entities.Project;
import devgames.entities.User;

import java.util.List;

/**
 * Created by Wouter on 1/9/2016.
 */
public interface UserService {
    public boolean validateUser(String username,String password);

    public List<Project> findAllProjectsOfUser(User user);
    public List<Commit> findAllCommitsOfUser(User user);

}
