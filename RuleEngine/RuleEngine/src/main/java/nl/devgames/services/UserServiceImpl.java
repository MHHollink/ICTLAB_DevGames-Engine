package nl.devgames.services;

import devgames.entities.Commit;
import devgames.entities.Project;
import devgames.entities.User;
import devgames.services.interfaces.UserService;

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
