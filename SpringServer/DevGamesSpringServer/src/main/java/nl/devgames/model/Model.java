package nl.devgames.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Model<M> {

    Long id;

    public Model() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if objects are the same, if Object id's are identical they should be the same!
     *
     * @param o the Other object of the chosen model
     * @return true if same, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null || getClass() != o.getClass()) return false;

        Model entity = (Model) o;

        return id.equals(entity.id);
    }

    /**
     * Convine method to create a single object from a {@link com.google.gson.JsonObject}
     *
     * @param object
     * @return
     */
    public abstract M createFromJsonObject(JsonObject object);

    /**
     * Creates a list of the {@link M} from a {@link com.google.gson.JsonArray}. The Objects are converted via {@link #createFromJsonObject(JsonObject)}
     *
     * @param array JSON array from Gson
     * @return List of objects from the given model type
     */
    public List<M> createFromJsonArray(JsonArray array) {
        List<M> list = new ArrayList<>();

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
