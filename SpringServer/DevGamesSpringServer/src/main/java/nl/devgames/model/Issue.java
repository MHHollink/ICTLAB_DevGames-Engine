package nl.devgames.model;

public class Issue extends Model {
    String key;

    String severity;
    String component;

    Integer startLine;
    Integer endLine;

    String status;
    String resolution;

    String message;
    Integer debt;

    Long creationDate;
    Long updateDate;
    Long closeDate;

    public Issue() {
    }

    public Issue(String key, String severity, String component, Integer startLine, Integer endLine, String status, String resolution, String message, Integer debt, Long creationDate, Long updateDate, Long closeDate) {
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public String getSeverity() {
        return severity;
    }

    public String getComponent() {
        return component;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public String getStatus() {
        return status;
    }

    public String getResolution() {
        return resolution;
    }

    public String getMessage() {
        return message;
    }

    public Integer getDebt() {
        return debt;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public Long getCloseDate() {
        return closeDate;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDebt(Integer debt) {
        this.debt = debt;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
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
