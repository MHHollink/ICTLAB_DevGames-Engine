package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.devgames.connection.database.dao.*;
import nl.devgames.model.*;
import nl.devgames.rest.errors.DatabaseOfflineException;
import nl.devgames.rest.errors.ReportParseExeption;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;

public class SQReportDTO {

    private int identifier;

	private Project project;
	private Long id;
	private User author;
	private Long timestamp;
	private ReportResultType buildResult;
	private List<Commit> commits;
	private List<Issue> issues;
	private List<Duplication> duplications;
	private Double score;
    private Boolean built;
    private Boolean saved;

    public SQReportDTO() {
		built = false;
        saved = false;
	}
	
	/**
     * Builds a Sonar Qube report from a json object
     *
     * @param json 		the report as json object
     * @return sqreport 		the new parsed report
     */
	public SQReportDTO buildFromJson(JsonObject json, String token) throws ReportParseExeption {
        try {
            identifier = new Random().nextInt();

            L.i("Parsing SonarQube report [%d]", identifier);
            Project project = new ProjectDao().queryByField("token", token).get(0);

            User author = parseAuthor(json.get("author").getAsString());
            ReportResultType resultType = parseResultType(json.get("result").getAsString());
            Long timestamp = parseTimeStamp(json.get("timestamp").getAsString());
            List<Commit> commitList = parseCommits(json.get("items").getAsJsonArray());
            List<Issue> issueList = parseIssues(json.get("issues").getAsJsonArray());
            List<Duplication> duplicationsList = parseDuplications(json);

            //return sqreport if valid data
            SQReportDTO sqreport = new SQReportDTO();
            sqreport.setProject(project);
            sqreport.setAuthor(author);
            sqreport.setBuildResult(resultType);
            sqreport.setTimestamp(timestamp);
            sqreport.setCommits(commitList);
            sqreport.setIssues(issueList);
            sqreport.setDuplications(duplicationsList);
            sqreport.setTimestamp(timestamp);

            L.i("Done parsing SonarQube report [%d]", identifier);
            sqreport.isBuilt(true);
            sqreport.setIdentifier(identifier);

            return sqreport;
        } catch (IndexOutOfBoundsException e) {
            L.e(e, "Getting project by token threw error, token is incorrect. token:'%s'", token);
            throw new ReportParseExeption("Token invalid");
        } catch (ConnectException e) {
            L.e(e, "Database offline");
            throw new DatabaseOfflineException();
        }
    }

    /**
     * Saves the parsed report to the neo4j database incl relationships
     * @throws ConnectException
     */
	public void saveReportToDatabase() throws ConnectException {
        if(!hasBeenBuilt()) {
            L.w("Hey developer! wake up! you forgot to build the report.");
            return;
        }
        if(hasBeenSaved()) {
            L.w("Hey developer! wake up! saving twice is really stupid.");
            return;
        }

        L.i("Saving report to database [%d]", identifier);

        //push push to database
        PushDao pushDAO = new PushDao();
        Push newPush = pushDAO.createIfNotExists(new Push(UUID.randomUUID().toString(), this.timestamp, this.score));

        //push->project
        pushDAO.saveRelationship(newPush, project);

        CommitDao commitDao = new CommitDao();
        commits.parallelStream().forEach(commit -> {
                    try {
                        Commit c = commitDao.createIfNotExists(commit);
                        pushDAO.saveRelationship(newPush, c);
                    } catch (ConnectException e) {
                        L.e("Database offline");
                        throw new DatabaseOfflineException();
                    }
                }
        );

        IssueDao issueDao = new IssueDao();
        issues.parallelStream().forEach(
                issue -> {
                    try {
                        Issue i = issueDao.createIfNotExists(issue);
                        pushDAO.saveRelationship(newPush, i);
                    } catch (ConnectException e) {
                        L.e("Database offline");
                        throw new DatabaseOfflineException();
                    }
                }
        );

        DuplicationDao duplicationDAO = new DuplicationDao();
        duplications.parallelStream().forEach(
                duplication -> {
                    try {
                        Duplication d = duplicationDAO.createIfNotExists(duplication);
                        pushDAO.saveRelationship(newPush, d);
                    } catch (ConnectException e) {
                        L.e("Database offline");
                        throw new DatabaseOfflineException();
                    }
                }
        );

        new UserDao().saveRelationship(author, newPush);

        saved = true;
        L.i("completed saving report to database [%d]", identifier);
    }

	/**
	 * ==========================Convenience methods=========================
	 */

	/**
	 * Parses the push author from the json object
	 * @param username		    the gitUsername of the user who pushed as string
	 * @return author			the author of the push
	 */
	private User parseAuthor(String username) throws ConnectException {
        try {
            author = new UserDao().queryByField("gitUsername", username).get(0);
        } catch (IndexOutOfBoundsException e) {
            L.e(e, "User queried by gitUsername threw error: gitUsername:'%s'", username);
            throw new ReportParseExeption("User with gitUsername:'%s' is not in the database or linked to the project");
        }
		return author;
	}
	
	/**
	 * Parses the push result from the json object
	 * @param buildResult		the build result of the report as string
	 * @return resultType		the push result as ReportResultType
	 */
	private ReportResultType parseResultType(String buildResult) {
		if(buildResult != null) {
			switch (buildResult) {
                case "SUCCESS": return ReportResultType.SUCCESS;
                case "FAILED": return ReportResultType.FAILED;

                default: return ReportResultType.UNDEFINED;
			}
		}
        throw new ReportParseExeption("Build result was null");
	}
	
	/**
	 * Parses the push timestamp from the json object
	 * @param buildTimestamp		the timestamp of the report as String
	 * @return timestamp		the push timestamp as a Long
	 */
	private Long parseTimeStamp(String buildTimestamp) {
		if(buildTimestamp != null) {
			try{
				return Long.parseLong(buildTimestamp);
			}
			catch(Exception e) {
                return System.currentTimeMillis();
			}
		}
        throw new ReportParseExeption("Time of the build was empty, could not parse!");
    }
	
	/**
	 * Parses the push commits from the json object
	 * @param commitsArray		the json array of the report holding the commits
	 * @return commits		the push commits as a List of commits
	 */
	private List<Commit> parseCommits(JsonArray commitsArray) {
        List<Commit> commits = new ArrayList<>();
        commitsArray.forEach(commit -> {
            commits.add(
                new Gson().fromJson(commit, Commit.class)
            );
        });
       if (commits.size() == 0) {
           L.e("we had 0 commits in the report, did something go wrong?");
           throw new ReportParseExeption("we had 0 commits in the report, did something go wrong?");
       }
        return commits;
	}
	
	/**
	 * Parses the push issues from the json object
	 * @param issuesArray		the json array of the report holding the issues
	 * @return issueList		the push issues as a List of issues
	 */
	private List<Issue> parseIssues(JsonArray issuesArray) {
        try {
            List<Issue> issues = new ArrayList<>();
            issuesArray.forEach(issue -> {
                IssueDTO dto = new Gson().fromJson(issue, IssueDTO.class);
                if (!dto.isValid())
                    throw new ReportParseExeption("Issues that we received were not correctly structured");
                issues.add(dto.toModel());
            });
            return issues;
        } catch (NullPointerException e) {
            L.e(e, "Error parsing issues");
            throw new ReportParseExeption("Issues could not be parsed.");
        }
	}
	
	/**
	 * Parses the push Duplications from the json object
	 * @param reportAsJson		the json object of the report
	 * @return duplicationsList		the push Duplications as a List of Duplications
	 */
	private List<Duplication> parseDuplications(JsonObject reportAsJson) {
		try {
            List<Duplication> duplicationsList = new ArrayList<>();
            JsonArray duplicationsArray = reportAsJson.get("duplications").getAsJsonArray();
            for (JsonElement duplicationElement : duplicationsArray) {
                //ONE duplication
                Duplication duplication = new Duplication();
                Set<DuplicationFile> duplicationFilesSet = new HashSet<>();
                JsonArray fileArray = duplicationElement.getAsJsonObject().get("files").getAsJsonArray();
                //for each duplication file
                for (int i = 0; i < fileArray.size(); i++) {
                    JsonObject object = fileArray.get(i).getAsJsonObject();
                    DuplicationFileDTO duplicationFile = new Gson().fromJson(object, DuplicationFileDTO.class);
                    //check if fields aren't empty
                    if (duplicationFile.isValid()) {
                        duplicationFilesSet.add(duplicationFile.toModel());
                    } else {
                        throw new ReportParseExeption("Received DuplicationFiles were not correctly structured");
                    }
                }
                duplication.setFiles(duplicationFilesSet);
                duplicationsList.add(duplication);
            }
            return duplicationsList;
        } catch (NullPointerException e) {
            L.e(e, "Error parsing duplications");
            throw new ReportParseExeption("Duplications could not be parsed.");
        }
	}

    /**
     * ==========================Getter/Setter methods=========================
     */
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ReportResultType getBuildResult() {
		return buildResult;
	}

	public void setBuildResult(ReportResultType buildResult) {
		this.buildResult = buildResult;
	}

	public List<Commit> getCommits() {
		return commits;
	}

	public void setCommits(List<Commit> commits) {
		this.commits = commits;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public List<Duplication> getDuplications() {
		return duplications;
	}

	public void setDuplications(List<Duplication> duplications) {
		this.duplications = duplications;
	}
	
	public Double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

    public void isBuilt(boolean built) {
        this.built = built;
    }

    public Boolean hasBeenBuilt() {
        return built;
    }

    public void isSaved(boolean saved){
        this.saved = saved;
    }

    public Boolean hasBeenSaved() {
        return saved;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }


}
