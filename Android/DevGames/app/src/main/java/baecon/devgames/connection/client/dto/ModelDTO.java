package baecon.devgames.connection.client.dto;

/**
 * A class definition that is identical to the structure of the JSON document that is returned by the back-end. This
 * definition is then used by {@link com.google.gson.Gson} to directly map the JSON document to a class instance.
 */
public interface ModelDTO<Model> {

    /**
     * Returns the ID. A ID is <strong>ALWAYS</strong> assigned by the back-end.
     *
     * @return The ID.
     */
    Long getId();

    /**
     * Returns a {@link Model} with as much fields populated as possible. Fields that should be populated with operations
     * that are potentially lengthy (like database), should be done on a non-UI thread.
     *
     * @return A {@link Model} with as much fields populated as possible
     */
    Model toModel();
}