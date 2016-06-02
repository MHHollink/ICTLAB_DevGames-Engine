package nl.devgames.model;

/**
 * TODO: Write class level documentation
 *
 * @author Marcel
 * @since 26-5-2016.
 */
public class Settings extends Model {

    Project project;
    double issuesPerCommitThreshold;
    double startScore;
    boolean pointStealing;
    boolean negativeScores;

    public Settings() {
    }

    public Settings(Project project, boolean pointStealing, double issuesPerCommitThreshold, boolean negativeScores, double startScore) {
        this.project = project;
        this.pointStealing = pointStealing;
        this.issuesPerCommitThreshold = issuesPerCommitThreshold;
        this.negativeScores = negativeScores;
        this.startScore = startScore;
    }

    public void setDefault() {
        this.project = null;
        this.pointStealing = false;
        this.issuesPerCommitThreshold = 10.0;
        this.issuesPerCommitThreshold = 1000.0;
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
