package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.Commit;

/**
 * Created by Marcel on 14-4-2016.
 */
public class CommitDTO extends ModelDTO<CommitDTO, Commit> {

    @Override
    public Commit toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public CommitDTO createFromJsonObject(JsonObject object) {
        return null;
    }
}
