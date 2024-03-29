package nl.devgames.model;

public class Commit extends Model {

    private String commitId;
    private String commitMsg;
    private long timestamp;

    public Commit() {

    }

    public Commit(String commitId, String commitMsg, long timeStamp) {
        this.commitId = commitId;
        this.commitMsg = commitMsg;
        this.timestamp = timestamp;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    public void setCommitMsg(String commitMsg) {
        this.commitMsg = commitMsg;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "commitId='" + commitId + '\'' +
                ", commitMsg='" + commitMsg + '\'' +
                ", timestamp=" + timestamp +
                "} " + super.toString();
    }

}


