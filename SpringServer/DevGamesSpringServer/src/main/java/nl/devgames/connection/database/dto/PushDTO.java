package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;

import java.util.Set;

/**
 * Created by Marcel on 14-4-2016.
 */
public class PushDTO extends ModelDTO<PushDTO, Push> {

    public Project project;
    public Set<Commit> commits;
    public Set<Issue> issues;
    public Set<Duplication> duplications;
    public long timestamp;

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
        return new Gson().fromJson(object, PushDTO.class);
    }
}
