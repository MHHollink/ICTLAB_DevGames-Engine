package nl.devgames.connection.database.dao;

import java.net.ConnectException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Dao<T, ID>{

    /**
     * Retrieves an object associated with a specific ID.
     *
     * @param id
     *            Identifier that matches a specific row in the database to find and return.
     * @return The object that has the ID field which equals id or null if no matches.
     */
    T queryForId(ID id) throws ConnectException;

    /**
     * Query for all of the items in the object table.
     *
     * @return A list of all of the objects in the table.
     */
    List<T> queryForAll() throws ConnectException;

    /**
     * Query for the items in the object table that match a simple where with a single field = value type of WHERE
     * clause.
     *
     * @return A list of the objects in the table that match the fieldName = value;
     */
    List<T> queryByField(String fieldName, Object value) throws ConnectException;

    /**
     * Query for the rows in the database that matches all of the field to value entries from the map passed in.
     */
    List<T> queryByFields(Map<String, Object> fieldValues) throws ConnectException;

    /**
     * Query for a data item in the table that has the same id as the data parameter.
     */
    T queryForSameId(T data) throws ConnectException;

    /**
     * Create a new row in the database from an object.
     *
     * @param data
     *            The data item that we are creating in the database.
     * @return The number of rows updated in the database. This should be 1.
     */
    int create(T data) throws ConnectException;

    /**
     * This is a convenience method to creating a data item but only if the ID does not already exist in the table. This
     * extracts the id from the data parameter, does a {@link #queryForId(Object)} on it, returning the data if it
     * exists. If it does not exist {@link #create(Object)} will be called with the parameter.
     *
     * @return Either the data parameter if it was inserted (now with the ID field set via the create method) or the
     *         data element that existed already in the database.
     */
    T createIfNotExists(T data) throws ConnectException;

    /**
     * Store the fields from an object to the database row corresponding to the id from the data parameter. If you have
     * made changes to an object, this is how you persist those changes to the database. You cannot use this method to
     * update the id field.
     *
     * @param data
     *            The data item that we are updating in the database.
     * @return The number of rows updated in the database. This should be 1.
     */
    int update(T data) throws ConnectException;

    /**
     * Delete the database row corresponding to the id from the data parameter.
     *
     * @param data
     *            The data item that we are deleting from the database.
     * @return The number of rows updated in the database. This should be 1.
     */
    int delete(T data) throws ConnectException;

    /**
     * Delete an object from the database that has an id.
     *
     * @param id
     *            The id of the item that we are deleting from the database.
     * @return The number of rows updated in the database. This should be 1.
     */
    int deleteById(ID id) throws ConnectException;

    /**
     * Delete a collection of objects from the database using an IN SQL clause. The ids are extracted from the datas
     * parameter and used to remove the corresponding rows in the database with those ids.
     *
     * @param datas
     *            A collection of data items to be deleted.
     * @return The number of rows updated in the database. This should be the size() of the collection.
     */
    int delete(Collection<T> datas) throws ConnectException;

    /**
     * Delete the objects that match the collection of ids from the database using an IN SQL clause.
     *
     * @param ids
     *            A collection of data ids to be deleted.
     * @return The number of rows updated in the database. This should be the size() of the collection.
     */
    int deleteIds(Collection<ID> ids) throws ConnectException;
}
