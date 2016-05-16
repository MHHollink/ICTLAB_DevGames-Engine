package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Duplication;
import nl.devgames.model.DuplicationFile;
import nl.devgames.utils.L;

import java.util.Set;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DuplicationDTO extends ModelDTO<DuplicationDTO, Duplication> {

    public Set<DuplicationFile> files;

    @Override
    public Duplication toModel() {
        Duplication duplication = new Duplication();

        duplication.setFiles(this.files);

        return duplication;
    }

    @Override
    public boolean isValid() {
        boolean valid = files != null;

        if(!valid) {
            L.w("Duplication is not valid! False indicates a problem: " +
                            "files:'%b'",
                    files != null
            );
        }

        return valid;
    }

    @Override
    public DuplicationDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, DuplicationDTO.class);
    }

    @Override
    public DuplicationDTO createFromNeo4jData(JsonObject data) {
        return null;
    }

    @Override
    public boolean equalsInContent(DuplicationDTO other) {
        return false;
    }
}
