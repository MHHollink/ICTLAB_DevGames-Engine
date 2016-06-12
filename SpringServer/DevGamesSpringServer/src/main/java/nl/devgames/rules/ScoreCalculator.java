package nl.devgames.rules;

import nl.devgames.connection.database.dto.SQReportDTO;

/**
 * Created by jorik on 12-6-2016.
 */
public interface ScoreCalculator {
    /**
     * Calculates score based on a Sonar Qube report
     * @param sqReportDTO the report which to calculate the score with
     * @return the score based on the sent in report as a double
     */
    public double calculateScoreFromReport(SQReportDTO sqReportDTO);
}
