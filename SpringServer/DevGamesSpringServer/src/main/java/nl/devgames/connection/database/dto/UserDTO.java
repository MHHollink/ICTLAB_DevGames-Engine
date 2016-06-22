package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.util.Set;

public class UserDTO extends ModelDTO<UserDTO, User> {

    public String username;
    public String gitUsername;

    public String firstName;
    public String tween;
    public String lastName;

    public Integer age;
    public String mainJob;

    public Set<Project> projects;
    public Set<Push> pushes;

    public String session;
    public String gcmId;

    public String password;
    public double totalScore;

    public boolean deleted;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.gitUsername = user.getGitUsername();
        this.firstName = user.getFirstName();
        this.tween = user.getTween();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.mainJob = user.getMainJob();
        this.projects = user.getProjects();
        this.pushes = user.getPushes();
        this.session = user.getSessionId();
        this.gcmId = user.getGcmId();
        this.password = user.getPassword();
        this.totalScore = user.getTotalScore();
    }

    @Override
    public User toModel() {
        if(!isValid()) return null;

        User user = new User();

        user.setId(this.id);
        user.setUsername(this.username);
        user.setGitUsername(this.gitUsername);
        user.setFirstName(this.firstName);
        user.setTween(this.tween);
        user.setLastName(this.lastName);
        user.setAge(this.age);
        user.setMainJob(this.mainJob);
        user.setProjects(this.projects);
        user.setPushes(this.pushes);
        user.setSessionId(this.session);
        user.setGcmId(this.gcmId);
        user.setPassword(this.password);
        user.setTotalScore(this.totalScore);

        return user;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isValid() {
        boolean valid =
                gitUsername != null &&
                        (deleted || username != null && password != null && firstName != null && lastName != null);

        if(!valid) {
            L.w("User is not valid! False indicates a problem: " +
                    "deleted: '%b', gitUsername:'%b', {username: '%b', password: '%b', firstName: '%b', lastName: '%b'}",
                    deleted,
                    gitUsername != null,
                    username != null,
                    password != null,
                    firstName != null,
                    lastName != null
            );
        }

        return valid;
    }

    @Override
    public UserDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, UserDTO.class);
    }

    @Override
    public UserDTO createFromNeo4jData(JsonObject data) {
        UserDTO dto = new UserDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(UserDTO other) {
        return toModel().equals(other.toModel());
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", firstName='" + firstName + '\'' +
                ", tween='" + tween + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", mainJob='" + mainJob + '\'' +
                ", projects=" + projects +
                ", pushes=" + pushes +
                ", session='" + session + '\'' +
                ", gcmId='" + gcmId + '\'' +
                ", password='" + password + '\'' +
                ", totalScore='" + totalScore + '\'' +
                "} " + super.toString();
    }
}
