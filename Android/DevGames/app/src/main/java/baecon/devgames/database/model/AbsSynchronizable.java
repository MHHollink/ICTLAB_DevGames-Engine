package baecon.devgames.database.model;

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
}
