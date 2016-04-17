package nl.devgames.model;

public class Issue extends Model {

    String severity;
    String component;

    int startLine;
    int endLine;

    String status;
    String resolution;

    String message;
    int debt;

    long creationDate;
    long updateDate;
    long closeDate;

    public Issue() {
    }

    public Issue(String severity, String component, int startLine, int endLine, String status, String resolution, String message, int debt, long creationDate, long updateDate, long closeDate) {
        this.severity = severity;
        this.component = component;
        this.startLine = startLine;
        this.endLine = endLine;
        this.status = status;
        this.resolution = resolution;
        this.message = message;
        this.debt = debt;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.closeDate = closeDate;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public long getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(long closeDate) {
        this.closeDate = closeDate;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "closeDate=" + getCloseDate() +
                ", updateDate=" + getUpdateDate() +
                ", creationDate=" + getCreationDate() +
                ", debt=" + getDebt() +
                ", message='" + getMessage() + '\'' +
                ", resolution='" + getResolution() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", endLine=" + getEndLine() +
                ", startLine=" + getStartLine() +
                ", component='" + getComponent() + '\'' +
                ", severity='" + getSeverity() + '\'' +
                "} " + super.toString();
    }
}
