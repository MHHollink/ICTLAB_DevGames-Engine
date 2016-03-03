package baecon.devgames.util;

import baecon.devgames.model.Commit;
import baecon.devgames.model.Project;
import baecon.devgames.model.User;

public class DummyHelper {

    public Project project;
    public User user;
    public Commit commit;

    private static DummyHelper instance;

    public static DummyHelper getInstance() {
        if(instance == null) instance = new DummyHelper();
        return instance;
    }

    private DummyHelper() {
        user = new User("Mjollnir94", "Mjollnir94");

        project = new Project(
                user, "DevGames",
                "Programming gamification");

        user.addProject(project);

        commit = new Commit(
                project, user,
                "Initial Commit",
                "4bfeac7f9d9cd23bf7bf282831223b232c8a21d7",
                "Feature_android", 31, 1457014531);

        user.addCommit(commit);
        project.addCommit(commit);
    }
}
