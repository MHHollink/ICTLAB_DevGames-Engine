package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.utils.L;

import java.util.Set;

public class PushDTO extends ModelDTO<PushDTO, Push> {

    public Project project;
    public Set<Commit> commits;
    public Set<Issue> issues;
    public Set<Duplication> duplications;
    public Long timestamp;
    public String key;

    @Override
    public Push toModel() {
        Push push = new Push();

        push.setId(id);
        push.setKey(key);
        push.setProject(this.project);
        push.setCommits(this.commits);
        push.setIssues(this.issues);
        push.setDuplications(this.duplications);
        push.setTimestamp(this.timestamp);

        return push;
    }

    @Override
    public boolean isValid() {
        boolean valid = project != null &&
                commits != null &&
                issues != null &&
                duplications != null &&
                timestamp != 0d;

        if(!valid) {
            L.w("Push is not valid! False indicates a problem: " +
                            "project:'%b', commits:'%b', issues:'%b', " +
                            "duplications: '%b', timesatmp: '%b'",
                    project != null,
                    commits != null,
                    issues != null,
                    duplications != null,
                    timestamp != 0d
            );
        }

        return valid;
    }

    @Override
    public PushDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, PushDTO.class);
    }

    @Override
    public PushDTO createFromNeo4jData(JsonObject data) {
        PushDTO dto = new PushDTO().createFromJsonObject(
                data.get("data").getAsJsonObject()
        );
        dto.id = data.get("id").getAsLong();
        return dto;
    }

    @Override
    public boolean equalsInContent(PushDTO o) {
        return
                this == o || o != null
                        && (project != null ? project.equals(o.project) : o.project == null
                        && (commits != null ? commits.equals(o.commits) : o.commits == null
                        && (issues != null ? issues.equals(o.issues) : o.issues == null
                        && (duplications != null ? duplications.equals(o.duplications) : o.duplications == null
                        && (timestamp != null ? timestamp.equals(o.timestamp) : o.timestamp == null)))));
    }


    @Override
    public String toString() {
        return "PushDTO{" +
                "project=" + project +
                ", commits=" + commits +
                ", issues=" + issues +
                ", duplications=" + duplications +
                ", timestamp=" + timestamp +
                ", key='" + key + '\'' +
                "} " + super.toString();
    }
}
