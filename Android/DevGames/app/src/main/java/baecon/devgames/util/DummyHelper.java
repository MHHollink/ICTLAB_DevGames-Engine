package baecon.devgames.util;

import baecon.devgames.database.model.Commit;
import baecon.devgames.database.model.Project;
import baecon.devgames.database.model.User;

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
        marcel = new User(5l, "Marcel", "Mjollnir94");
        evert = new User(1l, "Evert-Jan", "Evestar");
        jorik = new User(2l, "Jorik", "Jorikito");
        wouter = new User(3l, "Wouter", "0868307");
        jelle = new User(4l, "Jelle", "draikos");

        clarity = new Project(evert, "Clarity", "VR for Port of Rotterdam");
        devgames = new Project(marcel, "DevGames", "Gamification for Developers");
        adventuretrack = new Project(evert, "Adventure Track", "");

        init = new Commit(devgames, marcel, "Inital Commit", "", "master", 12, 1457288402, 104);
        android = new Commit(devgames, marcel, "First work in Android", "", "android_develop", 67, 1457288466, 87);
        gcm = new Commit(devgames, jorik, "Added some files for Google Cloud Messaging", "", "android_develop", 2, 1457288527, 32);
        rest = new Commit(devgames, wouter, "Server setup and Initial REST files", "", "webapp_develop", 51, 1457288630, 93);

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
    }
}
