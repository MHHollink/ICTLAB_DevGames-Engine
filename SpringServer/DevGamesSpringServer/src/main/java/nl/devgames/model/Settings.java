package nl.devgames.model;

import java.util.UUID;

/**
 * TODO: Write class level documentation
 *
 * @author Marcel
 * @since 26-5-2016.
 */
public class Settings extends Model {

    private String uuid; // using uuid for unique field
    Project project;
    double issuesPerCommitThreshold;
    double startScore;
    boolean pointStealing;
    boolean negativeScores;

    public Settings() {
        generateUUID();
    }

    public Settings(Project project, boolean pointStealing, double issuesPerCommitThreshold, boolean negativeScores, double startScore) {
        this.project = project;
        this.pointStealing = pointStealing;
        this.issuesPerCommitThreshold = issuesPerCommitThreshold;
        this.negativeScores = negativeScores;
        this.startScore = startScore;
        generateUUID();
    }

    public void generateUUID() {
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDefault() {
        this.project = null;
        this.pointStealing = false;
        this.issuesPerCommitThreshold = 10.0;
        this.startScore = 1000.0;
        this.negativeScores = false;
}

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }


    public boolean isPointStealing() {
        return pointStealing;
    }

    public void setPointStealing(boolean pointStealing) {
        this.pointStealing = pointStealing;
    }

    public double getIssuesPerCommitThreshold() {
        return issuesPerCommitThreshold;
    }

    public void setIssuesPerCommitThreshold(double issuesPerCommitThreshold) {
        this.issuesPerCommitThreshold = issuesPerCommitThreshold;
    }

    public boolean isNegativeScores() {
        return negativeScores;
    }

    public void setNegativeScores(boolean negativeScores) {
        this.negativeScores = negativeScores;
    }

    public double getStartScore() {
        return startScore;
    }

    public void setStartScore(double startScore) {
        this.startScore = startScore;
    }
}
