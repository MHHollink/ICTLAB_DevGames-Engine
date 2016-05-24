package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Issue;
import nl.devgames.utils.L;

/**
 * Created by Marcel on 14-4-2016.
 */
public class IssueDTO extends ModelDTO<IssueDTO, Issue> {

    public String key;

    public String severity;
    public String component;

    public Integer startLine;
    public Integer endLine;

    public String status;
    public String resolution;

    public String message;
    public Integer debt;

    public Long creationDate;
    public Long updateDate;
    public Long closeDate;

    @Override
    public Issue toModel() {
        Issue issue = new Issue();

        //issue.setId(id);
        issue.setKey(key);
        issue.setSeverity(this.severity);
        issue.setComponent(this.component);
        issue.setStartLine(this.startLine);
        issue.setEndLine(this.endLine);
        issue.setStatus(this.status);
        issue.setResolution(this.resolution);
        issue.setMessage(this.message);
        issue.setDebt(this.debt);
        issue.setCreationDate(this.creationDate);
        issue.setUpdateDate(this.updateDate);
        issue.setCloseDate(this.closeDate);

        return issue;
    }

    @Override
    public boolean isValid() {
        boolean valid = severity != null &&
                component != null &&
                startLine != Integer.MIN_VALUE &&
                endLine != Integer.MIN_VALUE &&
                status != null &&
//                resolution != null &&
                message != null &&
                debt != Integer.MIN_VALUE &&
                creationDate != null;
//                updateDate != null &&
//                closeDate != null;

        if(!valid) {
            L.w("Issue is not valid! False indicates a problem: " +
                            "severity:'%b', component:'%b', startLine:'%b', endLine:'%b', status:'%b', resolution:'%b', message:'%b', " +
                            "debt:'%b', creationDate:'%b', updateDate:'%b', closeDate:'%b'",
                            severity != null,
                            component != null,
                            startLine != Integer.MIN_VALUE,
                            endLine != Integer.MIN_VALUE,
                            status != null,
                            resolution != null,
                            message != null,
                            debt != Integer.MIN_VALUE,
                            creationDate != null,
                            updateDate != null,
                            closeDate != null
            );
        }

        return valid;
    }

    @Override
    public IssueDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, IssueDTO.class);
    }

    @Override
    public IssueDTO createFromNeo4jData(JsonObject data) {
        IssueDTO dto = new IssueDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(IssueDTO other) {
        return false;
    } // TODO: 23-5-2016  

    @Override
    public String toString() {
        return "IssueDTO{" +
                "severity='" + severity + '\'' +
                ", component='" + component + '\'' +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
                ", status='" + status + '\'' +
                ", resolution='" + resolution + '\'' +
                ", message='" + message + '\'' +
                ", debt=" + debt +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", closeDate=" + closeDate +
                "} " + super.toString();
    }
}
