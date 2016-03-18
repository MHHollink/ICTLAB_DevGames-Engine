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
}
