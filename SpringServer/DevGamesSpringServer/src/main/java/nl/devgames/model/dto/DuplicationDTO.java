package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;

import java.util.Set;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DuplicationDTO extends ModelDTO<DuplicationDTO, Duplication> {

    public Set<DuplicationFile> files;

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
        return new Gson().fromJson(object, DuplicationDTO.class);
    }
}
