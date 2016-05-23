package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Business;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import nl.devgames.utils.L;

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
        Business business = new Business();

        business.setName(this.name);
        business.setEmployees(this.employees);
        business.setProjects(this.projects);

        return business;
    }

    @Override
    public boolean isValid() {
        boolean valid = name != null &&
                employees != null &&
                projects != null;

        if(!valid) {
            L.w("Business is not valid! False indicates a problem: " +
                            "name:'%b', employees:'%b', projects:'%b'",
                    name != null,
                    employees != null,
                    projects != null
            );
        }

        return valid;
    }

    @Override
    public BusinessDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, BusinessDTO.class);
    }

    @Override
    public BusinessDTO createFromNeo4jData(JsonObject data) {
        BusinessDTO dto = new BusinessDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(BusinessDTO other) {
        return false;
    }

    @Override
    public String toString() {
        return "BusinessDTO{" +
                "name='" + name + '\'' +
                ", employees=" + employees +
                ", projects=" + projects +
                "} " + super.toString();
    }
}
