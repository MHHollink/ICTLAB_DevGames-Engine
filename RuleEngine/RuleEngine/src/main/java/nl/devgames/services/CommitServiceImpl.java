package nl.devgames.services;

import devgames.entities.Commit;
import devgames.services.interfaces.CommitService;

/**
 * Created by Wouter on 3/5/2016.
 */
public class CommitServiceImpl extends GenericService<Commit> implements CommitService {
    @Override
    public Class<Commit> getEntityType() {
        return Commit.class;
    }
}
