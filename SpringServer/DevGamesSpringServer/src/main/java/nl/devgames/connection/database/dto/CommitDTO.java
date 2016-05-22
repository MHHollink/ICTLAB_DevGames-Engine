package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Commit;
import nl.devgames.utils.L;

public class CommitDTO extends ModelDTO<CommitDTO, Commit> {

    public String commitId;
    public String commitMsg;
    public Long timestamp;

    @Override
    public Commit toModel() {
        Commit commit = new Commit();

        commit.setCommitId(this.commitId);
        commit.setCommitMsg(this.commitMsg);
        commit.setTimeStamp(this.timestamp);

        return commit;
    }

    @Override
    public boolean isValid() {
        boolean valid = commitId != null &&
                commitMsg != null &&
                timestamp != 0d;

        if(!valid) {
            L.w("Commit is not valid! False indicates a problem: " +
                            "commitId:'%b', commitMsg:'%b', timestamp:'%b'",
                    commitId != null,
                    commitMsg != null,
                    timestamp != 0d
            );
        }

        return valid;
    }

    @Override
    public CommitDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, CommitDTO.class);
    }

    @Override
    public CommitDTO createFromNeo4jData(JsonObject data) {
        return null;
    }

    @Override
    public boolean equalsInContent(CommitDTO other) {
        return false;
    }

    @Override
    public String toString() {
        return "CommitDTO{" +
                "commitId='" + commitId + '\'' +
                ", commitMsg='" + commitMsg + '\'' +
                ", timestamp=" + timestamp +
                "} " + super.toString();
    }
}
