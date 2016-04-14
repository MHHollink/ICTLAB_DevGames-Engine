package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.User;

public class UserDTO extends ModelDTO<UserDTO, User> {

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
        return null;
    }
}
