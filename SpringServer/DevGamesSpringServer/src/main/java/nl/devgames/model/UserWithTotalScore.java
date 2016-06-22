package nl.devgames.model;

/**
 * Created by jorik on 22-6-2016.
 */
public class UserWithTotalScore {
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String name;

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    private double totalScore;

    public UserWithTotalScore(Long id, String name, double totalScore) {
        this.id = id;
        this.name = name;
        this.totalScore = totalScore;
    }


}
