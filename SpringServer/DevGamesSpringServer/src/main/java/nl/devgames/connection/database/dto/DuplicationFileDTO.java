package nl.devgames.connection.database.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.DuplicationFile;
import nl.devgames.utils.L;

/**
 * Created by Marcel on 14-4-2016.
 */
public class DuplicationFileDTO extends ModelDTO<DuplicationFileDTO, DuplicationFile> {

    public String file;
    public Integer beginLine;
    public Integer endLine;
    public Integer size;

    @Override
    public DuplicationFile toModel() {
        DuplicationFile df = new DuplicationFile();

        df.setFile(file);
        df.setBeginLine(beginLine);
        df.setEndLine(endLine);
        df.setSize(size);

        return df;
    }

    @Override
    public boolean isValid() {
        boolean valid = file != null &&
                        beginLine != null &&
                        endLine != null &&
                        size != null;

        if(!valid) {
            L.w("DuplicationFile is not valid! False indicates a problem: " +
                    "file:'%b', beginLine:'%b', endLine:'%b', size:'%b'",
                    file != null,
                    beginLine != null,
                    endLine != null,
                    size != null
            );
        }

        return valid;
    }

    @Override
    public DuplicationFileDTO createFromJsonObject(JsonObject object) {
        return new Gson().fromJson(object, DuplicationFileDTO.class);
    }

    @Override
    public DuplicationFileDTO createFromNeo4jData(JsonObject data) {
        return null;
    }

    @Override
    public boolean equalsInContent(DuplicationFileDTO other) {
        return false;
    }

    @Override
    public String toString() {
        return "DuplicationFileDTO{" +
                "file='" + file + '\'' +
                ", beginLine=" + beginLine +
                ", endLine=" + endLine +
                ", size=" + size +
                "} " + super.toString();
    }
}
