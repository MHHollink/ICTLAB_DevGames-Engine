package nl.devgames.rules;

import com.google.gson.JsonObject;
import nl.devgames.model.Issue;
import nl.devgames.connection.database.dto.ReportResultType;
import nl.devgames.connection.database.dto.SQReportDTO;

import java.util.List;

public class ScoreCalculator {
	SQReportDTO sqReporDTO;
	
	public ScoreCalculator() {
		
	}

	/**
	 * Calculates score based on a Sonar Qube report
	 * @param sqReportDTO the report which to calculate the score with
	 * @return the score based on the sent in report as a double
	 */
	public double calculateScoreFromReport(SQReportDTO sqReportDTO, JsonObject settings) {
		this.sqReporDTO = sqReportDTO;
		double score = 1000;
		score = subtractBuildPoints(score);
		score = subtractIssuePoints(score);
		
		return score;
	}
	
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
	
	private double subtractIssuePoints(double score) {
		double newScore = score;
		List<Issue> issues = sqReporDTO.getIssues();
		
		
		return newScore;
	}
}
