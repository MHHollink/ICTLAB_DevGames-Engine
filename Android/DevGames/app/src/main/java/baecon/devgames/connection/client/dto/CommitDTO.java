package baecon.devgames.connection.client.dto;

import baecon.devgames.model.Commit;
import baecon.devgames.model.Project;
import baecon.devgames.model.User;

public class CommitDTO implements ModelDTO<Commit> {

    private long id;
    private String branch;
    private User committee;
    private int filesChanged;
    private String hash;
    private Project project;
    private long time;
    private String title;
    private double score;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Commit toModel() {
        Commit commit = new Commit();

        commit.setId(id);
        commit.setBranch(branch);
        commit.setCommittee(committee);
        commit.setFilesChanges(filesChanged);
        commit.setHash(hash);
        commit.setProject(project);
        commit.setTimestamp(time);
        commit.setTitle(title);
        commit.setScore(score);

        return commit;
    }

    public CommitDTO() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public User getCommittee() {
        return committee;
    }

    public void setCommittee(User committee) {
        this.committee = committee;
    }

    public int getFilesChanged() {
        return filesChanged;
    }

    public void setFilesChanged(int filesChanged) {
        this.filesChanged = filesChanged;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
