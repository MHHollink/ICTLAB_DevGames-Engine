package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;

import java.util.Set;

public class UserDTO extends ModelDTO<UserDTO, User> {

    public String username;
    public String gitUsername;

    public String firstName;
    public String tween;
    public String lastName;

    public int age;
    public String mainJob;

    public Set<Project> projects;
    public Set<Push> pushes;

    public String session;
    public String gcmId;

    @Override
    public User toModel() {

        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setGitUsername(gitUsername);
        user.setFirstName(firstName);
        user.setTween(tween);
        user.setLastName(lastName);
        user.setAge(age);
        user.setMainJob(mainJob);
        user.setProjects(projects);
        user.setPushes(pushes);
        user.setSessionId(session);
        user.setGcmId(gcmId);

        return user;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public UserDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, UserDTO.class);
    }
}
