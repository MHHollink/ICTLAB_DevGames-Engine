package baecon.devgames.database.dto;

import baecon.devgames.model.Commit;


public class CommitDTO implements ModelDTO<Commit> {
    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Commit toModel() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
