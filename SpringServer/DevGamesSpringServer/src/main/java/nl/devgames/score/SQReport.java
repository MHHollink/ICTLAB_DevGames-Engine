package nl.devgames.score;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.model.Commit;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.model.Issue;
import nl.devgames.model.Project;
import nl.devgames.model.User;

public class SQReport {
	private Project project;
	private User author;
	private long timeStamp;
	private ReportResultType buildResult;
	private List<Commit> commits;
	private List<Issue> issues;
	private List<Duplication> duplications;

	public SQReport() {
		
	}
	
	public SQReport(Project project, User author, long timeStamp, ReportResultType buildResult, 
			List<Commit> commits, List<Issue> issues, List<Duplication> duplications) {
		this.project = project;
		this.author = author;
		this.timeStamp = timeStamp;
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
	public SQReport buildFromJson(JsonObject reportAsJson) {
		System.out.println("================Parsing SonarQube report================");
		//build project
		Project project = parseProject(reportAsJson);
		
		//author
		User author = parseAuthor(reportAsJson);
		
		//build result
		String buildResult = reportAsJson.get("result").getAsString();
		ReportResultType resultType = ReportResultType.UNDEFINED;
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
			setBuildResult(resultType);
		}
		//timestamp
		String buildTimestamp = reportAsJson.get("timestamp").getAsString();
		long timestamp = System.currentTimeMillis();
		if(buildResult!=null) {
			try{
				 timestamp = Long.parseLong(buildTimestamp);
			}
			catch(Exception e) {
				System.out.println("invalid timestamp while parsing the report, set to current time");
				timestamp = System.currentTimeMillis();
			}
			setTimeStamp(timestamp);
		}
		//commits
		List<Commit> commitList = new ArrayList<>();
		JsonArray commitArray = reportAsJson.get("items").getAsJsonArray();
		for(JsonElement commitElement : commitArray) {			
			String commitId = commitElement.getAsJsonObject().get("commitId").getAsString();
			String commitMsg = commitElement.getAsJsonObject().get("commitMsg").getAsString();
			String commitDate = commitElement.getAsJsonObject().get("date").getAsString();
			Long commitTimestamp = null;
			try{
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
			    Date parsedDate = dateFormat.parse(commitDate);
			    commitTimestamp = parsedDate.getTime();
			}
			catch(Exception e){
				System.out.println("error parsing timestamp of commit with id: " + commitId + " , set to current time");
				commitTimestamp = System.currentTimeMillis();
			}
			if(commitId!=null && commitMsg!=null && commitTimestamp!=null) {
				commitList.add(new Commit(commitId, commitMsg, commitTimestamp));
			}
			else{
				System.out.println("error parsing commit with id: " + commitId);
			}
		}
		setCommits(commitList);
		//issues
		List<Issue> issueList = new ArrayList<>();
		JsonArray issueArray = reportAsJson.get("issues").getAsJsonArray();
		for(JsonElement issueElement : issueArray) {			
			String issueSeverity = issueElement.getAsJsonObject().get("severity").getAsString();
			String issueComponent = issueElement.getAsJsonObject().get("component").getAsString();
			//range
			JsonObject issueTextRange = issueElement.getAsJsonObject().get("textRange").getAsJsonObject();
			Integer issueStartLine = issueTextRange.get("startLine").getAsInt();
		    Integer issueEndLine = issueTextRange.get("endLine").getAsInt();
		    
		    String status = issueElement.getAsJsonObject().get("status").getAsString();
		    String resolution = issueElement.getAsJsonObject().get("resolution").getAsString();
		    String message = issueElement.getAsJsonObject().get("message").getAsString();
		    //TODO: convert time (context?) into int
		    String debtAsString = issueElement.getAsJsonObject().get("debt").getAsString();
		    Integer debt = 0;
		    //timestamps
		    String creationDate = issueElement.getAsJsonObject().get("creationDate").getAsString();
		    String updateDate = issueElement.getAsJsonObject().get("updateDate").getAsString();
		    String closeDate = issueElement.getAsJsonObject().get("closeDate").getAsString();
		    Long creationTimestamp = null;
		    Long updateTimestamp = null;;
		    Long closeTimestamp = null;;
			try{
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
			    Date parsedCreationDate = dateFormat.parse(creationDate);
			    Date parsedUpdateDate = dateFormat.parse(updateDate);
			    Date parsedCloseDate = dateFormat.parse(closeDate);
			    creationTimestamp = parsedCreationDate.getTime();
			    updateTimestamp = parsedUpdateDate.getTime();
			    closeTimestamp = parsedCloseDate.getTime();
			}
			catch(Exception e){
				System.out.println("error parsing timestamp of issue with message: " + message);
				creationTimestamp = System.currentTimeMillis();
			    updateTimestamp = System.currentTimeMillis();
			    closeTimestamp = System.currentTimeMillis();
			}
			if(issueSeverity!=null && issueComponent!=null && issueStartLine!=null && issueEndLine!=null
					&& status!=null && resolution!=null && message!=null && debtAsString!=null) {
				issueList.add(new Issue(issueSeverity, issueComponent, issueStartLine, issueEndLine,
						status, resolution, message, debt, creationTimestamp, updateTimestamp,
						closeTimestamp));
			}
			else{
				System.out.println("error parsing issue with message: " + message);
			}
		}
		//duplications
		List<Duplication> duplicationsList = new ArrayList<>();
		JsonArray duplicationsArray = reportAsJson.get("duplications").getAsJsonArray();
		for(JsonElement duplicationElement : duplicationsArray) {
			//ONE duplication
			Duplication duplication = new Duplication();
			Set<DuplicationFile> duplicationFilesSet = new HashSet<>();
			//TODO: HOE KRIJG IK ALLE FILES ALS ZE DEZELFDE NAAM HEBBEN??
			JsonArray fileArray = duplicationElement.getAsJsonArray();
			for(int i = 0; i < fileArray.size(); i++)
			{
				//for each file
				String fileName = "unknown";
				try{
					DuplicationFile duplicationFile = new DuplicationFile();
					JsonObject file = fileArray.get(i).getAsJsonObject();
					fileName = file.get("name").getAsString();
				    Integer fileFromLine = file.get("from").getAsInt();
				    Integer fileSize = file.get("size").getAsInt();
				    Integer fileEndLine = fileFromLine + fileSize;
					//add to set
					duplicationFilesSet.add(duplicationFile);
				}
				catch(Exception e) {
					System.out.println("error parsing duplication with name: " + fileName);
				}
			}
			duplication.setFiles(duplicationFilesSet);
			duplicationsList.add(duplication);
		}
		setDuplications(duplicationsList);
		
		SQReport sqreport = new SQReport();
		sqreport.setProject(project);
		sqreport.setAuthor(author);
		sqreport.setBuildResult(resultType);
		sqreport.setTimeStamp(timestamp);
		sqreport.setCommits(commitList);
		sqreport.setIssues(issueList);
		sqreport.setDuplications(duplicationsList);;setTimeStamp(timestamp);
		//setters here
		return sqreport;
	}

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
			setProject(project);
		}
		else {
			System.out.println("error parsing project name");
		}
		return project;
	}
	
	/**
	 * Parses the push author from the json object
	 * @param reportAsJson		the json object of the report
	 * @return project			the project with the project name
	 */
	private User parseAuthor(JsonObject reportAsJson) {
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
			setAuthor(author);	
		}
		else {
			System.out.println("error parsing author");
		}
		return author;
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

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
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
	
}
