package baecon.devgames.connection.synchronization;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.io.Serializable;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.connection.client.dto.UserDTO;
import baecon.devgames.connection.task.poll.ModelPollTask;
import baecon.devgames.connection.task.push.ModelPushTask;
import baecon.devgames.events.PushTaskDoneEvent;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.User;
import baecon.devgames.model.update.UserUpdate;

/**
 * Created by Marcel on 13-3-2016.
 */
public class UserManager extends AbsModelManager<User, UserDTO, UserUpdate, PushTaskDoneEvent>{

    private static UserManager instance;

    public static UserManager get(Context context) {
        if (instance == null) {
            instance = new UserManager(DevGamesApplication.get(context));
        }
        return instance;
    }

    public static UserManager get(Fragment fragment) {
        if (instance == null) {
            instance = new UserManager(DevGamesApplication.get(fragment));
        }
        return instance;
    }

    protected UserManager(DevGamesApplication app) {
        super(app);
    }

    @Override
    protected ModelPushTask<User, UserUpdate> newUpdateTask(DevGamesApplication app, Long id) {
        return null;
    }

    @Override
    protected ModelPollTask<User, UserUpdate, UserDTO> newPollTask(Context context) {
        return null;
    }


    @Override
    public long getBackgroundPollingInterval() {
        return 0;
    }

    @Override
    public long getForegroundPollingInterval() {
        return 0;
    }

    @Override
    public boolean isAllowedToSyncInBackground() {
        return false;
    }

    @Override
    public void create(User user) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void update(Long id, String field, Serializable value) {

    }

    @Override
    public void delete(User user) {

    }
}
