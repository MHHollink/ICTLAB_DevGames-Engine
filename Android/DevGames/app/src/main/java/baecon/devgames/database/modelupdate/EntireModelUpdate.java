package baecon.devgames.database.modelupdate;

import baecon.devgames.model.ISynchronizable;

/**
 * An {@link IModelUpdate} that involves the whole model instance to be updated. This is most of the
 * time used to create the instance in the back-end.
 */
public interface EntireModelUpdate<Model extends ISynchronizable> extends IModelUpdate<Model> {

    /**
     * Returns the serialized model containing all changes that have to be synchronized. This is the actual content that
     * will be sent to the back-end.
     *
     * @return The serialized model containing all changes that have to be synchronized.
     */
    Model getModel();
}