package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Business;
import nl.devgames.model.Project;
import nl.devgames.model.User;

import java.util.Set;

/**
 * Created by Marcel on 14-4-2016.
 */
public class BusinessDTO extends ModelDTO<BusinessDTO, Business>{

    public String name;

    public Set<User> employees;
    public Set<Project> projects;

    @Override
    public Business toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public BusinessDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, BusinessDTO.class);
    }
}
