package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.User;

public class UserDTO extends ModelDTO<User> {


    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public User createFromJsonObject(JsonObject object) {
        return null;
    }
}
