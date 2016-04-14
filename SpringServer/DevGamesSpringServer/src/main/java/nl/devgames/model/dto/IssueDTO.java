package nl.devgames.model.dto;

import com.google.gson.JsonObject;
import nl.devgames.model.Issue;

/**
 * Created by Marcel on 14-4-2016.
 */
public class IssueDTO extends ModelDTO<IssueDTO, Issue> {
    @Override
    public Issue toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public IssueDTO createFromJsonObject(JsonObject object) {
        return null;
    }
}
