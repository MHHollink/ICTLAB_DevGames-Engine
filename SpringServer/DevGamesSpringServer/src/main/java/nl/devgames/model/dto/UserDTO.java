package nl.devgames.model.dto;

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

    public String SessionId;
    public String gcmId;

    @Override
    public User toModel() {
        return null;
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
