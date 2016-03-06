package baecon.devgames.util;

import baecon.devgames.model.Commit;
import baecon.devgames.model.Project;
import baecon.devgames.model.User;

public class DummyHelper {

    public Project clarity, devgames, adventuretrack;
    public User marcel, evert, jorik, jelle, wouter;
    public Commit init, android, gcm, rest;

    private static DummyHelper instance;

    public static DummyHelper getInstance() {
        if(instance == null) instance = new DummyHelper();
        return instance;
    }

    private DummyHelper() {
        marcel = new User("Marcel", "Mjollnir94");
        evert = new User("Evert-Jan", "Evestar");
        jorik = new User("Jorik", "Jorikito");
        wouter = new User("Wouter");
        jelle = new User("Jelle");

        clarity = new Project(evert, "Clarity", "VR for Port of Rotterdam");
        devgames = new Project(marcel, "DevGames", "Gamification for Developers");
        adventuretrack = new Project(evert, "Adventure Track", "");

        init = new Commit(devgames, marcel, "Inital Commit", "", "master", 12, 1457288402);
        android = new Commit(devgames, marcel, "First work in Android", "", "android_develop", 67, 1457288466);
        gcm = new Commit(devgames, jorik, "Added some files for Google Cloud Messaging", "", "android_develop", 2, 1457288527);
        rest = new Commit(devgames, wouter, "Server setup and Initial REST files", "", "webapp_develop", 51, 1457288630);

        marcel.addProject(devgames, clarity, adventuretrack);
        evert.addProject(devgames, clarity, adventuretrack);
        jorik.addProject(devgames, clarity);
        jelle.addProject(devgames);
        wouter.addProject(devgames);

        marcel.addCommit(init, android);
        jorik.addCommit(gcm);
        wouter.addCommit(rest);

        devgames.addCommit(init, android, gcm, rest);
        devgames.addDeveloper(evert, marcel, jorik, jelle, wouter);
        adventuretrack.addDeveloper(evert, marcel);
        clarity.addDeveloper(evert, jorik, marcel);

        devgames.addScore(78);
        adventuretrack.addScore(301);
        clarity.addScore(204);
    }
}
