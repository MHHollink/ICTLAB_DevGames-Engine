package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.DuplicationFile;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DuplicationFileDTO extends ModelDTO<DuplicationFileDTO, DuplicationFile> {

    public String file;
    public int beginLine;
    public int endLine;
    public int size;

    @Override
    public DuplicationFile toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public DuplicationFileDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, DuplicationFileDTO.class);
    }
}
