package baecon.devgames.events;

import java.util.HashSet;

/**
 * Created by Marcel on 16-3-2016.
 */
public class UsersUpdatedEvent extends SynchronizableModelUpdatedEvent {
    public UsersUpdatedEvent(Integer result, HashSet<Long> removed, HashSet<Long> added, HashSet<Long> updated) {
        super();
    }
}
