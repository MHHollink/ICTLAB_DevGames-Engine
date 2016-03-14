package baecon.devgames.model;

import com.j256.ormlite.field.DatabaseField;

public abstract class AbsSynchronizable implements ISynchronizable {

    @DatabaseField(columnName = Column.ID, id = true)
    protected Long id;

    @DatabaseField(columnName = Column.STATE)
    protected State state;

    public AbsSynchronizable() {
        this(0l);
    }

    public AbsSynchronizable(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public State getState() {
        return state != null ? state : State.UP_TO_DATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbsSynchronizable)) return false;

        AbsSynchronizable that = (AbsSynchronizable) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AbstractSynchronizable{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    /**
     * Checks whether the content of two objects (of the same type) did change.
     *
     * <p>Compares as follows:</p>
     * <ul>
     * <li>{@code a == null && b == null} &rarr; {@code false}</li>
     * <li>{@code a != null && b == null} &rarr; {@code false}</li>
     * <li>{@code a == null && b != null} &rarr; {@code false}</li>
     * <li>{@code a != null && b != null} &rarr; {@code a.contentChanged(b)}</li>
     * </ul>
     *
     * @param a
     *         Object A, will be compared to object B
     * @param b
     *         Object b, will be compared to object A
     *
     * @return {@code a.contentEquals(b)}, or false if {@code a} or {@code b} is null
     */
    public static <T extends AbsSynchronizable> boolean contentEquals(T a, T b) {
        return a != null && b != null && a.contentEquals(b);
    }

    /**
     * A null safe equals check.
     *
     * <p>Compares as follows:</p>
     * <ul>
     * <li>{@code a == null && b == null} &rarr; {@code true}</li>
     * <li>{@code a != null && b == null} &rarr; {@code false}</li>
     * <li>{@code a == null && b != null} &rarr; {@code false}</li>
     * <li>{@code a != null && b != null} &rarr; {@code a.equals(b)}</li>
     * </ul>
     *
     * @param a
     *         Object A, will be compared to object B
     * @param b
     *         Object b, will be compared to object A
     *
     * @return See comparison table
     */
    public static <T> boolean equals(T a, T b) {
        boolean equal;
        if (a == null && b == null) {
            equal = true;
        }
        else if (a == null || b == null) {
            equal = false;
        }
        else {
            equal = a.equals(b);
        }

        return equal;
    }

}
