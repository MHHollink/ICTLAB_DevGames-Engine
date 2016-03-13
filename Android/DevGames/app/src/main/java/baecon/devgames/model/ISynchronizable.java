package baecon.devgames.model;

import java.io.Serializable;

import baecon.devgames.connection.synchronization.IModelManager;

/**
 * <p>An object that is used in the back-end and on this device (probably on more devices as well).
 * Every time the {@link IModelManager} polls the back-end for a new snapshot of the object.
 * This object is saved in a local database.</p>
 *
 * <p><strong>Identifiers</strong> Every object has a ID, everywhere the same in the whole system. It is possible
 * tough, that an object is created by the user, and cannot be synchronized to the back-end yet, due to no internet
 * connection. Since the back-end has to assign a ID, and the user has to be able to see the object in the mean time,
 * the object has a local identifier as well. This local id is only used on this device and <strong>NEVER</strong> send
 * to the back-end. This local id is in the local database used as the primary key.</p>
 *
 * <p><strong>Equality</strong> An object is equal when the ID matches with the other object. If the ID is not
 * available, the local id is used to check for equality.</p>
 */
public interface ISynchronizable extends Serializable {

    enum State {
        NEW,
        UP_TO_DATE,
        HAS_UN_SYNCED_CHANGES,
        HAS_BLOCKING_CHANGES,
        FLAGGED_FOR_DELETE
    }

    class Column {
        public static final String ID = "id";
        public static final String STATE = "state";
    }

    /**
     * Returns the ID. A ID is <strong>ALWAYS</strong> assigned by the back-end.
     *
     * @return The ID.
     */
    Long getId();

    /**
     * Set the ID.
     *
     * @param id
     *         The id.
     */
    void setId(Long id);

    /**
     * Returns the state.
     *
     * @return The {@link State}
     */
    State getState();

    /**
     * Sets the {@link State}.
     *
     * @param state The {@link State}
     */
    void setState(State state);

    /**
     * Returns {@code true} when ANY of the fields (except {@link #getId()}) has changed. It is
     * up to the developer to implement whether a field is 'changed' or not. Otherwise false.
     * <p/>
     *
     * @param other
     *         The object of the same Class to compare.
     *
     * @return {@code true} when ANY fields (except localId and uuid) have changed in comparison to the {@code other}.
     */
    boolean contentEquals(Object other);
}