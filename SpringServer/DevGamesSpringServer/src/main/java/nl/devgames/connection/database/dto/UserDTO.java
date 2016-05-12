package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.model.User;
import nl.devgames.utils.L;

import java.util.Iterator;
import java.util.List;
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

        return user;
    }

    @Override
    public boolean isValid() {
        boolean valid = username != null &&
                gitUsername != null &&
                firstName != null &&
                lastName != null &&
                age != 0 &&
                mainJob != null;

        if(!valid) {
            L.w("User is not valid! False indicates a problem: " +
                            "username:'%b', gitUsername:'%b', firstName:'%b', " +
                            "lastName:'%b', age:'%b', mainJob:'%b'",
                    username != null,
                    gitUsername != null,
                    firstName != null,
                    lastName != null,
                    age != 0,
                    mainJob != null
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
}
