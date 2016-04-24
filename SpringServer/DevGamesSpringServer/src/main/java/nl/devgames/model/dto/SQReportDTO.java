package nl.devgames.model.dto;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.User;
import nl.devgames.utils.L;

public class SQReportDTO {
	private Project project;
	private long id;
	private User author;
	private long timestamp;
	private ReportResultType buildResult;
	private List<Commit> commits;
	private List<Issue> issues;
	private List<Duplication> duplications;
	private double score;
    private boolean valid = true;

	public SQReportDTO() {
		
	}
	
	public SQReportDTO(Project project, User author, long timestamp, ReportResultType buildResult,
			List<Commit> commits, List<Issue> issues, List<Duplication> duplications) {
		this.project = project;
		this.author = author;
		this.timestamp = timestamp;
		this.buildResult = buildResult;
		this.commits = commits;
		this.issues = issues;
		this.duplications = duplications;
	}
	
	/**
     * Builds a Sonar Qube report from a json object
     *
     * @param reportAsJson 		the report as json object
     * @return sqreport 		the new parsed report
     */
	public SQReportDTO buildFromJson(JsonObject reportAsJson) throws Exception {
		L.i("Parsing SonarQube report");
		//build project
		Project project = parseProject(reportAsJson);
		
		//author
		User author = parseAuthor(reportAsJson);
		
		//build result
		ReportResultType resultType = parseResultType(reportAsJson);
		
		//timestamp
		Long timestamp = parseTimeStamp(reportAsJson);
		
		//commits
		List<Commit> commitList = parseCommits(reportAsJson);
		
		//issues
		List<Issue> issueList = parseIssues(reportAsJson);
		
		//duplications
		List<Duplication> duplicationsList = parseDuplications(reportAsJson);

		//return sqreport if valid data
		SQReportDTO sqreport = new SQReportDTO();
		if(valid) {
			//setters here
			sqreport.setProject(project);
			sqreport.setAuthor(author);
			sqreport.setBuildResult(resultType);
			sqreport.setTimestamp(timestamp);
			sqreport.setCommits(commitList);
			sqreport.setIssues(issueList);
			sqreport.setDuplications(duplicationsList);
			sqreport.setTimestamp(timestamp);
//		sqreport.setScore(score);
			L.og("Done parsing SonarQube report");
		}
		else{
			//throw exception
			throw new Exception("error parsing sonar qube qreport");
		}
		return sqreport;
	}
	
	/**
     * Saves the report data to the neo4j database
     */
	public void saveReportToDatabase() throws Exception {
		if(valid) {
//			NODES
            //push push to database
			 Neo4JRestService.getInstance().postQuery(
	                 "CREATE (n:Push { " +
	                         "id: '%d', timestamp: '%d', score: '%f'})",
					 this.getId(),
					 this.getTimestamp(),
					 this.getScore()
	         );
		    //push commits to database
			for (Commit commit : this.getCommits()) {
                Neo4JRestService.getInstance().postQuery(
                        "CREATE (n:Commit { " +
                                "commitId: '%s', commitMsg: '%s', timestamp: %d })",
                        commit.getCommitId(),
                        commit.getCommitMsg(),
                        commit.getTimeStamp()
                );
            }
            //push issues to database
            for (Issue issue : this.getIssues()) {
                Neo4JRestService.getInstance().postQuery(
                        "CREATE (n:Issue { " +
								"issueId: '%d', " +
                                "severity: '%s', component: '%s', message: '%s', " +
                                "status: '%s', resolution: '%s', dept: %d, " +
                                "startLine: %d, endLine: %d, creationDate : %d," +
                                "updateData: %d, closeData: %d })",
                        issue.getId(),
						issue.getSeverity(),
                        issue.getComponent(),
                        issue.getMessage(),
                        issue.getStatus(),
                        issue.getResolution(),
                        issue.getDebt(),
                        issue.getStartLine(),
                        issue.getEndLine(),
                        issue.getCreationDate(),
                        issue.getUpdateDate(),
                        issue.getCloseDate()
                );
            }
            //push duplications to neo4j database
            for (Duplication duplication : this.getDuplications()) {
                Neo4JRestService.getInstance().postQuery(
                        "CREATE (n:Duplication { " +
								"id: '%d' })",
						duplication.getId()
                );
                //push duplication files to neo4j database
                for (DuplicationFile duplicationFile : duplication.getFiles()) {
                    Neo4JRestService.getInstance().postQuery(
                            "CREATE (n:DuplicationFile)"
                    );
                }
            }
//			RELATIONSHIPS
			//push push relations to database
			for (Commit commit : this.getCommits()) {
				Neo4JRestService.getInstance().postQuery(
						"MATCH (a:Push { id: '%d' }), (b:Commit { id: '%s' }) " +
								"CREATE (a)-[:contains_commits]->(b)",
						this.getId(),
						commit.getCommitId()
				);
			}
			//push issue relations to database
			for (Issue issue : this.getIssues()) {
				Neo4JRestService.getInstance().postQuery(
						"MATCH (a:Push { id: '%d' }), (b:Issue { id: '%d' }) " +
								"CREATE (a)-[:contains_issues]->(b)",
						this.getId(),
						issue.getId()
				);
			}
			//push duplication to database
			for(Duplication duplication : this.getDuplications()) {
				Neo4JRestService.getInstance().postQuery(
						"MATCH (a:Push { id: '%d' }), (b:Duplication { id: '%d' }) " +
								"CREATE (a)-[:contains_duplications]->(b)",
						this.getId(),
						duplication.getId()
				);
				//push duplication files to database
				for(DuplicationFile duplicationFile : duplication.getFiles()) {
					Neo4JRestService.getInstance().postQuery(
							"MATCH (a:Duplication { id: '%d' }), (b:DuplicationFile { id: '%d' }) " +
									"CREATE (a)-[:contains_files]->(b)",
							duplication.getId(),
							duplicationFile.getId()
					);
				}
			}
        }
		else {
			throw new Exception("cannot save report due to invalid data");
		}
	}
	
//	private void getScoreBasedOnReport() {
//		setScore(new ScoreCalculator().calculateScoreFromReport(this));
//	}

	/**
	 * ==========================Convenience methods=========================
	 */
	
	/**
	 * Parses the project name from the json object
	 * @param reportAsJson		the json object of the report
	 * @return project			the project with the project name
	 */
	private Project parseProject(JsonObject reportAsJson) {
		String projectName = reportAsJson.get("projectName").getAsString();
		Project project = new Project();
		if(projectName!=null) {
			project.setName(projectName);
		}
		else {
			valid = false;
			L.e("error parsing project name");
		}
		return project;
	}
	
	/**
	 * Parses the push author from the json object
	 * @param reportAsJson		the json object of the report
	 * @return project			the project with the project name
	 */
	private User parseAuthor(JsonObject reportAsJson) {
		//default
		String pushAuthor = reportAsJson.get("author").getAsString();
		User author = new User();
		if(pushAuthor!=null) {
			String[] names = pushAuthor.split("\\s+");
			if(names.length == 1) {
				author.setFirstName(names[0]);
			}
			else if(names.length == 2) {
				author.setFirstName(names[0]);
				author.setLastName(names[1]);
			}
			else if(names.length == 3) {
				author.setFirstName(names[0]);
				author.setTween(names[1]);
				author.setFirstName(names[2]);
			}
		}
		else {
			L.og("error parsing author");
			valid = false;
		}
		return author;
	}
	
	/**
	 * Parses the push result from the json object
	 * @param reportAsJson		the json object of the report
	 * @return resultType		the push result as ReportResultType
	 */
	private ReportResultType parseResultType(JsonObject reportAsJson) {
		//default
		ReportResultType resultType = ReportResultType.UNDEFINED;
		String buildResult = reportAsJson.get("result").getAsString();
		if(buildResult!=null) {
			switch (buildResult) {
			case "SUCCESS":
				resultType = ReportResultType.SUCCESS;
				break;
			case "FAILED":
				resultType = ReportResultType.FAILED;
				break;
			default:
				resultType = ReportResultType.UNDEFINED;
				break;
			}
		}
		else {
			valid = false;
		}
		return resultType;
	}
	
	/**
	 * Parses the push timestamp from the json object
	 * @param reportAsJson		the json object of the report
	 * @return timestamp		the push timestamp as a Long
	 */
	private Long parseTimeStamp(JsonObject reportAsJson) {
		//default
		Long timestamp = System.currentTimeMillis();
		String buildTimestamp = reportAsJson.get("timestamp").getAsString();
		if(buildTimestamp!=null) {
			try{
				timestamp = Long.parseLong(buildTimestamp);
			}
			catch(Exception e) {
				L.og("invalid timestamp while parsing the report, set to current time");
				timestamp = System.currentTimeMillis();
			}
		}
		else {
			valid = false;
		}
		return timestamp;
	}
	
	/**
	 * Parses the push commits from the json object
	 * @param reportAsJson		the json object of the report
	 * @return commitsList		the push commits as a List of commits
	 */
	private List<Commit> parseCommits(JsonObject reportAsJson) {
		//default
		List<Commit> commitList = new ArrayList<>();
		JsonArray commitArray = reportAsJson.get("items").getAsJsonArray();
		for(JsonElement commitElement : commitArray) {	
			JsonObject object = commitElement.getAsJsonObject();
			CommitDTO commit = new Gson().fromJson(object, CommitDTO.class);
			//check if fields aren't empty
			if(commit.isValid()) {
				commitList.add(commit.toModel());
			}
			else {
				valid = false;
			}
//			String commitId = commitElement.getAsJsonObject().get("commitId").getAsString();
//			String commitMsg = commitElement.getAsJsonObject().get("commitMsg").getAsString();
//			String commitDate = commitElement.getAsJsonObject().get("date").getAsString();
//			Long commitTimestamp = null;
//			try{
//				//parse date to epoch
//			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
//			    Date parsedDate = dateFormat.parse(commitDate);
//			    commitTimestamp = parsedDate.getTime();
//			}
//			catch(Exception e){
//				L.og("error parsing timestamp of commit with id: %d ,set to current time" + commitId);
//				commitTimestamp = System.currentTimeMillis();
//			}
//			if(commitId!=null && commitMsg!=null && commitTimestamp!=null) {
//				commitList.add(new Commit(commitId, commitMsg, commitTimestamp));
//			}
//			else{
//				L.og("error parsing commit with id: %s", commitId);
//			}
		}
		return commitList;
	}
	
	/**
	 * Parses the push issues from the json object
	 * @param reportAsJson		the json object of the report
	 * @return issueList		the push issues as a List of issues
	 */
	private List<Issue> parseIssues(JsonObject reportAsJson) {
		//default
		List<Issue> issueList = new ArrayList<>();
		JsonArray issueArray = reportAsJson.get("issues").getAsJsonArray();
		for(JsonElement issueElement : issueArray) {
            JsonObject object = issueElement.getAsJsonObject();
            IssueDTO issue = new Gson().fromJson(object, IssueDTO.class);
            //check if fields aren't empty
            if(issue.isValid()) {
                issueList.add(issue.toModel());
            }
            else {
                valid = false;
            }
//			String issueSeverity = issueElement.getAsJsonObject().get("severity").getAsString();
//			String issueComponent = issueElement.getAsJsonObject().get("component").getAsString();
//			//range
//			JsonObject issueTextRange = issueElement.getAsJsonObject().get("textRange").getAsJsonObject();
//			Integer issueStartLine = issueTextRange.get("startLine").getAsInt();
//		    Integer issueEndLine = issueTextRange.get("endLine").getAsInt();
//
//		    String status = issueElement.getAsJsonObject().get("status").getAsString();
//		    String resolution = issueElement.getAsJsonObject().get("resolution").getAsString();
//		    String message = issueElement.getAsJsonObject().get("message").getAsString();
//
//		    //convert debt to int
//		    String debtAsString = "";
//		    Integer debt = 0;
//		    try{
//		        debtAsString = issueElement.getAsJsonObject().get("debt").getAsString();
//			    debt = Integer.parseInt(debtAsString.substring(0, debtAsString.length()-3));
//		    }
//		    catch(Exception e) {
//		    	L.og("error parsing debt of issue with message: %s", message);
//		    }
//
//		    //timestamps
//		    String creationDate = issueElement.getAsJsonObject().get("creationDate").getAsString();
//		    String updateDate = issueElement.getAsJsonObject().get("updateDate").getAsString();
//		    String closeDate = issueElement.getAsJsonObject().get("closeDate").getAsString();
//		    Long creationTimestamp = null;
//		    Long updateTimestamp = null;;
//		    Long closeTimestamp = null;;
//			try{
//				//parse date to epoch
//			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
//			    Date parsedCreationDate = dateFormat.parse(creationDate);
//			    Date parsedUpdateDate = dateFormat.parse(updateDate);
//			    Date parsedCloseDate = dateFormat.parse(closeDate);
//			    creationTimestamp = parsedCreationDate.getTime();
//			    updateTimestamp = parsedUpdateDate.getTime();
//			    closeTimestamp = parsedCloseDate.getTime();
//			}
//			catch(Exception e){
//				L.og("error parsing timestamp of issue with message: %s", message);
//				creationTimestamp = System.currentTimeMillis();
//			    updateTimestamp = System.currentTimeMillis();
//			    closeTimestamp = System.currentTimeMillis();
//			}
//			if(issueSeverity!=null && issueComponent!=null && issueStartLine!=null && issueEndLine!=null
//					&& status!=null && resolution!=null && message!=null && debtAsString!=null) {
//				issueList.add(new Issue(issueSeverity, issueComponent, issueStartLine, issueEndLine,
//						status, resolution, message, debt, creationTimestamp, updateTimestamp,
//						closeTimestamp));
//			}
//			else{
//				System.out.println("error parsing issue with message: " + message);
//			}
		}
		return issueList;
	}
	
	/**
	 * Parses the push Duplications from the json object
	 * @param reportAsJson		the json object of the report
	 * @return duplicationsList		the push Duplications as a List of Duplications
	 */
	private List<Duplication> parseDuplications(JsonObject reportAsJson) {
		//default
		List<Duplication> duplicationsList = new ArrayList<>();
		JsonArray duplicationsArray = reportAsJson.get("duplications").getAsJsonArray();
		for(JsonElement duplicationElement : duplicationsArray) {
			//ONE duplication
			Duplication duplication = new Duplication();
			Set<DuplicationFile> duplicationFilesSet = new HashSet<>();
			JsonArray fileArray = duplicationElement.getAsJsonObject().get("files").getAsJsonArray();
            //for each duplication file
			for(int i = 0; i < fileArray.size(); i++)
			{
                JsonObject object = fileArray.get(i).getAsJsonObject();
                DuplicationFileDTO duplicationFile = new Gson().fromJson(object, DuplicationFileDTO.class);
                //check if fields aren't empty
                if(duplicationFile.isValid()) {
                    duplicationFilesSet.add(duplicationFile.toModel());
                }
                else {
                    valid = false;
                }
//				//for each file
//				String fileName = "unknown";
//				try{
//					DuplicationFile duplicationFile = new DuplicationFile();
//					JsonObject file = fileArray.get(i).getAsJsonObject();
//					fileName = file.get("name").getAsString();
//				    Integer fileFromLine = file.get("from").getAsInt();
//				    Integer fileSize = file.get("size").getAsInt();
//				    Integer fileEndLine = fileFromLine + fileSize;
//					//add to set
//					duplicationFilesSet.add(duplicationFile);
//				}
//				catch(Exception e) {
//					System.out.println("error parsing duplication with name: " + fileName);
//				}
			}
			duplication.setFiles(duplicationFilesSet);
			duplicationsList.add(duplication);
		}
		return duplicationsList;
	}

	/**
	 * getters and setters
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
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
