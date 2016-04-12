package nl.devgames.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Set;

public class User extends Model<User> {

    private String username;
    private String gitUsername;

    private String firstName;
    private String tween;
    private String lastName;

    private int age;
    private String mainJob;

    private Set<Project> projects;
    private Set<Push> pushes;

    private String SessionId;
    private String gcmId;

    public User() {
    }

    public User(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, Set<Project> projects, Set<Push> pushes, String sessionId, String gcmId) {
        this.username = username;
        this.gitUsername = gitUsername;
        this.firstName = firstName;
        this.tween = tween;
        this.lastName = lastName;
        this.age = age;
        this.mainJob = mainJob;
        this.projects = projects;
        this.pushes = pushes;
        SessionId = sessionId;
        this.gcmId = gcmId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setTween(String tween) {
        this.tween = tween;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMainJob(String mainJob) {
        this.mainJob = mainJob;
    }

    public Set<Push> getPushes() {
        return pushes;
    }

    public void setPushes(Set<Push> pushes) {
        this.pushes = pushes;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getTween() {
        return tween;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getMainJob() {
        return mainJob;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", firstName='" + firstName + '\'' +
                ", tween='" + tween + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", mainJob='" + mainJob + '\'' +
                ", projects=" + projects +
                ", pushes=" + pushes +
                ", SessionId='" + SessionId + '\'' +
                ", gcmId='" + gcmId + '\'' +
                "} " + super.toString();
    }

    @Override
    public User createFromJsonObject(JsonObject object) {
        User o = new Gson().fromJson(object.get("data"), User.class);
        o.setId(object.get("id").getAsLong());
        return o;
    }
}
