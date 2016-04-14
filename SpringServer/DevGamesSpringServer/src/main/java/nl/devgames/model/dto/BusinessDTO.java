package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Business;

/**
 * Created by Marcel on 14-4-2016.
 */
public class BusinessDTO extends ModelDTO<BusinessDTO, Business>{

    @Override
    public Business toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        boolean valid;
        return false;
    }

    @Override
    public BusinessDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, BusinessDTO.class);
    }
}
