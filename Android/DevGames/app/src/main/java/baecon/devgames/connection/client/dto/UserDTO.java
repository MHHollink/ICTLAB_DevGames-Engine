package baecon.devgames.connection.client.dto;

import android.text.TextUtils;

import java.util.Set;

import baecon.devgames.model.User;

public class UserDTO implements ModelDTO<User> {

    private Long id;
    private String username;
    private String gitUsername;
    private Set<ProjectDTO> projectDTOs;
    private Set<CommitDTO> commitDTOs;
    private String gcmkey;

    public UserDTO(User user){
        id = user.getId();
        username = user.getUsername();
        gitUsername = user.getGitUsername();

    }

    public UserDTO() {
    }

    @Override
    public User toModel() {

        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setGitUsername(gitUsername);
        user.setGcmKey(gcmkey);

        for (ProjectDTO dto : projectDTOs) {
            user.addProject(dto.toModel());
        }
        for (CommitDTO dto : commitDTOs) {
            user.addCommit(dto.toModel());
        }

        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public Set<ProjectDTO> getProjectDTOs() {
        return projectDTOs;
    }

    public void setProjectDTOs(Set<ProjectDTO> projectDTOs) {
        this.projectDTOs = projectDTOs;
    }

    public Set<CommitDTO> getCommitDTOs() {
        return commitDTOs;
    }

    public void setCommitDTOs(Set<CommitDTO> commitDTOs) {
        this.commitDTOs = commitDTOs;
    }

    public String getGcmkey() {
        return gcmkey;
    }

    public void setGcmkey(String gcmkey) {
        this.gcmkey = gcmkey;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", projectDTOs=" + projectDTOs +
                ", commitDTOs=" + commitDTOs +
                ", gcmkey='" + gcmkey + '\'' +
                '}';
    }
}
