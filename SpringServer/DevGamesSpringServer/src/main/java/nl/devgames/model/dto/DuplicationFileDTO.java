package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.DuplicationFile;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DuplicationFileDTO extends ModelDTO<DuplicationFileDTO, DuplicationFile> {
    @Override
    public DuplicationFile toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public DuplicationFileDTO createFromJsonObject(JsonObject object) {
        return null;
    }
}
