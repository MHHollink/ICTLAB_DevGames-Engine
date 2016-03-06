package nl.devgames.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Wouter on 3/5/2016.
 */
public abstract class Entity {
    @JsonProperty("id")
    Long id;
    public Entity(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

