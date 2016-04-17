package nl.devgames.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Model {

    Long id;

    public Model() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if objects are the same, if Object id's are identical they should be the same!
     *
     * @param o the Other object of the chosen model
     * @return true if same, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null || getClass() != o.getClass()) return false;

        Model entity = (Model) o;

        return id.equals(entity.id);
    }


}
