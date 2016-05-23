package nl.devgames.model;

import java.util.Set;
import java.util.UUID;

public class Duplication extends Model {

    public enum Relations {
        HAS_FILE
    }

    private String uuid; // using uuid for unique field
    private Set<DuplicationFile> files;

    public Duplication() {
        generateUUID();
    }

    public void generateUUID() {
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
