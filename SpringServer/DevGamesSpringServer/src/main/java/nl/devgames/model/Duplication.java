package nl.devgames.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class Duplication extends Model {

    Set<DuplicationFile> files;
    long duplicationId;

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

    public long getDuplicationId() {
        return duplicationId;
    }

    public void setDuplicationId(long id) {
        this.duplicationId = id;
    }

    @Override
    public String toString() {
        return "Duplication{" +
                "files=" + files +
                '}';
    }
}
