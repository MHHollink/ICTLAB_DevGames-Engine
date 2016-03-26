package nl.devgames.model;

/**
 * Created by Marcel on 25-3-2016.
 */
public class UserWithPassword extends User {

    private String password;

    public UserWithPassword(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, String password) {
        super(username, gitUsername, firstName, tween, lastName, age, mainJob);
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
