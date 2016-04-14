package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.Duplication;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DuplicationDTO extends ModelDTO<DuplicationDTO, Duplication> {
    @Override
    public Duplication toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public DuplicationDTO createFromJsonObject(JsonObject object) {
        return null;
    }
}
