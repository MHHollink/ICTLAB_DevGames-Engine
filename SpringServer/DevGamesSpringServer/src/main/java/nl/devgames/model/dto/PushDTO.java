package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.Push;
import nl.devgames.utils.L;

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
        Push push = new Push();

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
}
