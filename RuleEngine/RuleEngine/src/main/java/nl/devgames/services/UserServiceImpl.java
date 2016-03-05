package nl.devgames.services;

import nl.devgames.entities.Commit;
import nl.devgames.entities.Project;
import nl.devgames.entities.User;
import nl.devgames.services.interfaces.UserService;

import java.util.List;

/**
 * Created by Wouter on 3/5/2016.
 */
public class UserServiceImpl extends GenericService<User> implements UserService {
    @Override
    public Class<User> getEntityType() {
        return User.class;
    }

    @Override
    public boolean validateUser(String username, String password) {
        return false;
    }

    @Override
    public List<Project> findAllProjectsOfUser(User user) {
        return null;
    }

    @Override
    public List<Commit> findAllCommitsOfUser(User user) {
        return null;
    }
}
