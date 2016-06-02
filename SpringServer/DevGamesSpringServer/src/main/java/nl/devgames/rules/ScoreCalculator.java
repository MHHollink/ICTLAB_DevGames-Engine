package nl.devgames.rules;

import nl.devgames.connection.database.dto.ReportResultType;
import nl.devgames.connection.database.dto.SQReportDTO;
import nl.devgames.model.Commit;
import nl.devgames.model.Issue;
import nl.devgames.model.Settings;
import nl.devgames.utils.L;

import java.util.List;

public class ScoreCalculator {
	SQReportDTO sqReporDTO;
	double issuePerCommitThreshold;
	double startScore;
	boolean isPointStealing;
	boolean isNegativeScores;

	public ScoreCalculator(Settings settings) {

        if(settings == null) {
            Settings s = new Settings();
            s.setDefault();
            issuePerCommitThreshold = s.getIssuesPerCommitThreshold();
            isPointStealing = s.isPointStealing();
			isNegativeScores = s.isNegativeScores();
			startScore = s.getStartScore();
        }
	}

	/**
	 * Calculates score based on a Sonar Qube report
	 * @param sqReportDTO the report which to calculate the score with
	 * @return the score based on the sent in report as a double
	 */
	public double calculateScoreFromReport(SQReportDTO sqReportDTO) {
		L.i("Calculating score for report [%d]", sqReportDTO.getIdentifier());

		this.sqReporDTO = sqReportDTO;
		double score = startScore;

		//build points
		score = subtractBuildPoints(score);
		//issue points
		score = subtractIssuePoints(score, issuePerCommitThreshold);

		L.d("Calculated score for report [%d]", sqReportDTO.getIdentifier());
		return score;
	}

	//======================subtract scoring method========================
	
	private double subtractBuildPoints(double score) {
		double newScore = score;
		if(this.sqReporDTO.getBuildResult()==ReportResultType.SUCCESS) {
			// TODO ?
		}
		else if(this.sqReporDTO.getBuildResult()==ReportResultType.FAILED){
			newScore -= 500.0;
		}
		return newScore;
	}
	
	private double subtractIssuePoints(double score, double threshold) {
		double newScore = score;
		List<Issue> issues = sqReporDTO.getIssues();
		List<Commit> commits = sqReporDTO.getCommits();
		double issuesPerCommit = issues.size() / commits.size();
		//subtract 10 per issue
		newScore -= (50 * issues.size());
		//return 50 points if under threshold
		if(issuesPerCommit <= threshold) {
			newScore += 50.0;
		}
		//reset if score < 0
		if(!isNegativeScores && newScore < 0.0) {
			newScore = 0.0;
		}
		return newScore;
	}

}
