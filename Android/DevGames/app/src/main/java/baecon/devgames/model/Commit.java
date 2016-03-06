package baecon.devgames.model;

public class Commit {

    private Project project;
    private User committee;
    private String title;
    private String hash;
    private String branch;
    private int filesChanges; // temp
    private long timestamp;

    public Commit(Project project, User committee, String title, String hash, String branch, int filesChanges, long timestamp) {
        this.project = project;
        this.committee = committee;
        this.title = title;
        this.hash = hash;
        this.branch = branch;
        this.filesChanges = filesChanges;
        this.timestamp = timestamp;
    }


}
