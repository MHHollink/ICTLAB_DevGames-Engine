package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.Push;

/**
 * Created by Marcel on 14-4-2016.
 */
public class PushDTO extends ModelDTO<PushDTO, Push> {
    @Override
    public Push toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public PushDTO createFromJsonObject(JsonObject object) {
        return null;
    }
}
