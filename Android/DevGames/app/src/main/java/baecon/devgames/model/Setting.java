package baecon.devgames.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "settings")
public class Setting implements Serializable {

    public static class Column {
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }

    /**
     * The key that is pointing to the username, or the empty string when
     * the user is not logged in.
     */
    public static final String USERNAME = "username";

    /**
     * The key that is pointing to the user id, or the empty string when
     * the user is not logged in.
     */
    public static final String USER_ID = "user_id";

    /**
     * The key that is pointing to a boolean flag indicating if the user is
     * properly logged in (if the username and password were accepted).
     */
    public static final String SESSION_ID = "session_id";





    // The unique key of the setting.
    @DatabaseField(columnName = Column.KEY, id = true)
    private String key;

    // The value of this setting.
    @DatabaseField(columnName = Column.VALUE)
    private String value;

    /**
     * Creates a new instance of a Setting.
     * <p/>
     * Empty constructor needed by ORMLite!
     */
    public Setting() {
    }

    /**
     * Creates a new instance of a Setting with a given `key` and `value`.
     *
     * @param key
     *         the unique key of this setting.
     * @param value
     *         the value of this setting.
     */
    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
