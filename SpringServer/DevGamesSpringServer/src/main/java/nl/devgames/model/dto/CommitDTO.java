package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Commit;

public abstract class CommitDTO extends ModelDTO<CommitDTO, Commit> {

    public String commitId;
    public String commitMsg;
    public long timeStamp;

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
        return new Gson().fromJson(object, CommitDTO.class);
    }
}
