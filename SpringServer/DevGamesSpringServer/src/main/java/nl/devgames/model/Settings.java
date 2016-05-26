package nl.devgames.model;

/**
 * TODO: Write class level documentation
 *
 * @author Marcel
 * @since 26-5-2016.
 */
public class Settings {

    Project project;
    String scoreMethod;
    double issuesPerCommitThreshold;
    boolean pointStealing;
    boolean negativeScores;

    public Settings() {
    }

    public Settings(Project project, String scoreMethod, boolean pointStealing, double issuesPerCommitThreshold, boolean negativeScores) {
        this.project = project;
        this.scoreMethod = scoreMethod;
        this.pointStealing = pointStealing;
        this.issuesPerCommitThreshold = issuesPerCommitThreshold;
        this.negativeScores = negativeScores;
    }

    public void setDefault() {
        this.project = null;
        this.scoreMethod = "SUB";
        this.pointStealing = false;
        this.issuesPerCommitThreshold = 10.0;
        this.negativeScores = false;
}

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getScoreMethod() {
        return scoreMethod;
    }

    public void setScoreMethod(String scoreMethod) {
        this.scoreMethod = scoreMethod;
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
}
