package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Issue;

/**
 * Created by Marcel on 14-4-2016.
 */
public class IssueDTO extends ModelDTO<IssueDTO, Issue> {

    public String severity;
    public String component;

    public int startLine;
    public int endLine;

    public String status;
    public String resolution;

    public String message;
    public int debt;

    public long creationDate;
    public long updateDate;
    public long closeDate;

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
        return new Gson().fromJson(object, IssueDTO.class);
    }
}
