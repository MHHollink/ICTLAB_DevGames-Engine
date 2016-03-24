package nl.devgames.model;

public class Commit extends AbsModel {

    private String hash;
    private String description;
    private int filesChanged;
    private long timeStamp;

    private Project project;

    public Commit() {

    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFilesChanged() {
        return filesChanged;
    }

    public void setFilesChanged(int filesChanged) {
        this.filesChanged = filesChanged;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "hash='" + hash + '\'' +
                ", description='" + description + '\'' +
                ", filesChanged=" + filesChanged +
                ", timeStamp=" + timeStamp +
                ", project=" + project +
                '}';
    }
}


