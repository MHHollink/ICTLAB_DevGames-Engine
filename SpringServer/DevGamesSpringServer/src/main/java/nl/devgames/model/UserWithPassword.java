package nl.devgames.model;

import com.google.gson.JsonObject;

import java.util.Set;

/**
 * Created by Marcel on 25-3-2016.
 */
public class UserWithPassword extends User {

    private String password;

    public UserWithPassword(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, Set<Project> projects, Set<Push> pushes, String sessionId, String gcmId, String password) {
        super(username, gitUsername, firstName, tween, lastName, age, mainJob, projects, pushes, sessionId, gcmId);
        this.password = password;
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
