package nl.devgames.model;

import java.util.HashSet;
import java.util.Set;

public class Duplication {

    Set<DuplicationFile> files;

    public Duplication() {
        files = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Duplication{" +
                "files=" + files +
                '}';
    }
}
