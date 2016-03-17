package baecon.devgames.events;

/**
 * Created by Marcel on 17-3-2016.
 */
public class LogoutEvent {
    public final boolean hasUnSynchronizedWork;

    public LogoutEvent(boolean hasUnSynchronizedWork) {
        this.hasUnSynchronizedWork = hasUnSynchronizedWork;
    }
}