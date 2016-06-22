package nl.devgames.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties({"sessionId"})
public class User extends Model {

    public enum Relations {
        IS_DEVELOPING,
        HAS_PUSHED,
        HAS_ACHIEVEMENT
    }

    private String username;
    private String gitUsername;

    private String firstName;
    private String tween;
    private String lastName;

    private Integer age;
    private String mainJob;

    private Set<Project> projects;
    private Set<Push> pushes;

    private String sessionId;
    private String gcmId;

    private String password;

    public User() {
    }

    public User(String username, String firstName, String gitUsername, String tween, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.gitUsername = gitUsername;
        this.tween = tween;
        this.lastName = lastName;
        this.password = password;
    }

    public User(String username, String gitUsername, String firstName, String tween, String lastName, int age, String mainJob, Set<Project> projects, Set<Push> pushes, String sessionId, String gcmId, String password) {
        this(username,firstName,gitUsername,tween,lastName,password);

        this.age = age;
        this.mainJob = mainJob;

        this.projects = projects;
        this.pushes = pushes;

        this.sessionId = sessionId;
        this.gcmId = gcmId;
    }

    public String getUsername() {
        return username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Push> getPushes() {
        return pushes;
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

    public Integer getAge() {
        return age;
    }

    public String getMainJob() {
        return mainJob;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
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

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setMainJob(String mainJob) {
        this.mainJob = mainJob;
    }

    public void setPushes(Set<Push> pushes) {
        this.pushes = pushes;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public void setPassword(String password) {
        this.password = password;
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
                ", sessionId='" + sessionId + '\'' +
                ", gcmId='" + gcmId + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (!Objects.equals(age, user.age)) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (gitUsername != null ? !gitUsername.equals(user.gitUsername) : user.gitUsername != null) return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        if (tween != null ? !tween.equals(user.tween) : user.tween != null) return false;
        if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
        if (mainJob != null ? !mainJob.equals(user.mainJob) : user.mainJob != null) return false;
        if (projects != null ? !projects.equals(user.projects) : user.projects != null) return false;
        if (pushes != null ? !pushes.equals(user.pushes) : user.pushes != null) return false;
        if (sessionId != null ? !sessionId.equals(user.sessionId) : user.sessionId != null) return false;
        if (gcmId != null ? !gcmId.equals(user.gcmId) : user.gcmId != null) return false;
        return password != null ? password.equals(user.password) : user.password == null;

    }
}
