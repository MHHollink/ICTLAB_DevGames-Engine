package baecon.devgames.database.modelupdate;

import java.io.Serializable;

import baecon.devgames.model.ISynchronizable;

/**
 * An {@link IModelUpdate} that involves just one field of the model that has to be updated. For
 * synchronization, this saves bandwidth because we can send just a part of the model instead of the whole model.
 */
public interface SingleFieldModelUpdate<Model extends ISynchronizable> extends IModelUpdate<Model> {

    /**
     * Returns the column name of the field that has to be updated
     *
     * @return The column name of the field
     */
    String getField();

    /**
     * Returns the value of the field that has to be updated.
     *
     * @return The value of the field that has to be updated.
     */
    Serializable getValue();
}