package nl.devgames.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbsModel {

    @JsonProperty("id")
    Long id;

    public AbsModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null || getClass() != o.getClass()) return false;

        AbsModel entity = (AbsModel) o;

        return id.equals(entity.id);

    }

    @Override
    public int hashCode() {
        return (id == null) ? -1 : id.hashCode();
    }
}
