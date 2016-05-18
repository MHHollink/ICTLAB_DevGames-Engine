package nl.devgames.connection.database.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.utils.L;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelDTO<
        ModelDTO extends nl.devgames.connection.database.dto.ModelDTO,
        Model extends nl.devgames.model.Model>
{

    /**
     * This represents the ID that is used in the Neo4J database;
     */
    public Long id;

    /**
     * Method that can be used on all the DTO's to get a Model class from the respective DTO
     * @return Object specified as respective Model class
     */
    public abstract Model toModel();

    /**
     * A Valid check that is used to check if all fields for a DTO are not-null
     * @return Boolean{True: if all fields are set, False: if one or more fields are not set}
     */
    public abstract boolean isValid();

    /**
     * Convine method to create a single object from a {@link com.google.gson.JsonObject}
     *
     * @param object A Json object that contains the fields from the DTO
     * @return DTO with specified fields set
     */
    public abstract ModelDTO createFromJsonObject(JsonObject object);

    /**
     * @param data
     * @return
     */
    public abstract ModelDTO createFromNeo4jData(JsonObject data);


    public abstract boolean equalsInContent(ModelDTO other);

    /**
     * Creates a list of the {@link Model} from a {@link com.google.gson.JsonArray}. The Objects are converted via {@link #createFromJsonObject(JsonObject)}
     *
     * @param array JSON array from Gson
     * @return List of objects from the given model type
     */
    public List<ModelDTO> createFromJsonArray(JsonArray array) {
        List<ModelDTO> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(
                    createFromJsonObject(
                            array.get(i).getAsJsonObject()
                    )
            );
        }
        return list;
    }

    /**
     * This method takes in a direct response from neo4j, it parses it - checks for errors - returns a {@link com.google.gson.JsonArray} of the data
     *
     * Example data:
     * <code>
     *      {
     *          "id":274,
     *          "labels":["User"],
     *          "data":
     *          {
     *              "lastName":"Heilema",
     *              "username":"Evestar",
     *              "mainJob":"Backend developer",
     *              "firstName":"...",
     *              ...
     *          }
     *      }
     * </code>
     *
     * @param jsonString    The String value returned from {@link nl.devgames.connection.database.Neo4JRestService#post(String)} or {@link nl.devgames.connection.database.Neo4JRestService#postQuery(String, Object...)}
     * @return
     *          {@link com.google.gson.JsonArray} as seen as above.
     * @throws KnownInternalServerError
     *          Might be thrown when the neo4j response contains errors.
     */
    public static JsonArray getNeo4JData(String jsonString) throws KnownInternalServerError {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject(); // parse neo4j response

        JsonArray errors = json.get("errors").getAsJsonArray(); // get the list of errors

        if (errors.size() != 0) { // Check if there are more the 0 errors
            for (JsonElement error : errors) L.e(error.getAsJsonObject().get("message").getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errors); // throws exception with errors
        }

        return json
                .get("results")
                .getAsJsonArray();
    }

    public static JsonObject findFirst(String json) {
        return getNeo4JData(json).get(0).getAsJsonObject().get("data")
                .getAsJsonArray().get(0).getAsJsonObject().get("row")
                .getAsJsonArray().get(0).getAsJsonObject();
    }

    public static List<JsonObject> findAll(String json) {
        List<JsonObject> objects = new ArrayList<>();

        JsonArray array = getNeo4JData(json).get(0).getAsJsonObject().get("data").getAsJsonArray();

        for (int i = 0; i < array.size(); i++ ) {
            objects.add(array.get(i).getAsJsonObject().get("row")
                     .getAsJsonArray().get(0).getAsJsonObject()
            );
        }

        return objects;
    }

    @Override
    public String toString() {
        return "ModelDTO{" +
                "id=" + id +
                '}';
    }
}
