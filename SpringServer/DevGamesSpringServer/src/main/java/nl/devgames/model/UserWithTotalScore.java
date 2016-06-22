package nl.devgames.model;

/**
 * Created by jorik on 22-6-2016.
 */
public class UserWithTotalScore {
    private Long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String username;

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    private double totalScore;

    public UserWithTotalScore(Long id, String name, double totalScore) {
        this.id = id;
        this.username = name;
        this.totalScore = totalScore;
    }


}
