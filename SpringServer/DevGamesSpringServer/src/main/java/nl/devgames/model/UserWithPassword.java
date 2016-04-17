package nl.devgames.model;

import java.util.Set;

/**
 * This class is used for the {@link User}'s that need a password. It is used in the {@link nl.devgames.rest.controller.UserController#createNewUser(UserWithPassword)} to save a password.
 * This class i only used for creation of uses and updating a users password.
 */
public class UserWithPassword extends User {

    private String password;

    public UserWithPassword(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, Set<Project> projects, Set<Push> pushes, String sessionId, String gcmId, String password) {
        super(username, gitUsername, firstName, tween, lastName, age, mainJob, projects, pushes, sessionId, gcmId);
        this.password = password;
    }

    public UserWithPassword() {
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserWithPassword{" +
                "password='" + password + '\'' +
                "} " + super.toString();
    }
}
