package nl.devgames.model;

import java.util.HashSet;
import java.util.Set;

public class Duplication extends Model {

    public enum Relations {
        HAS_FILE
    }

    Set<DuplicationFile> files;

    public Duplication() {
        files = new HashSet<>();
    }

    public Duplication(Set<DuplicationFile> files) {
        this.files = files;
    }

    public Set<DuplicationFile> getFiles() {
        return files;
    }

    public void setFiles(Set<DuplicationFile> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "Duplication{" +
                "files=" + files +
                '}';
    }
}
