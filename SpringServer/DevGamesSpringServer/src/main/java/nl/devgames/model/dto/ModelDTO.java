package nl.devgames.model.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nl.devgames.model.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelDTO<D extends ModelDTO, M extends Model> {

    public abstract M toModel();
    public abstract boolean isValid();

    /**
     * Convine method to create a single object from a {@link com.google.gson.JsonObject}
     *
     * @param object
     * @return
     */
    public abstract D createFromJsonObject(JsonObject object);

    /**
     * Creates a list of the {@link M} from a {@link com.google.gson.JsonArray}. The Objects are converted via {@link #createFromJsonObject(JsonObject)}
     *
     * @param array JSON array from Gson
     * @return List of objects from the given model type
     */
    public List<D> createFromJsonArray(JsonArray array) {
        List<D> list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            list.add(
                    createFromJsonObject(
                            array.get(i).getAsJsonObject()
                    )
            );
        }

        return list;
    }
}
