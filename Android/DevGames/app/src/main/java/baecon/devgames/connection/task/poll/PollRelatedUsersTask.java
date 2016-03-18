package baecon.devgames.connection.task.poll;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.util.HashSet;
import java.util.List;

import baecon.devgames.connection.client.DevGamesClient;
import baecon.devgames.connection.client.dto.UserDTO;
import baecon.devgames.connection.synchronization.AbsModelManager;
import baecon.devgames.connection.synchronization.UserManager;
import baecon.devgames.database.DBHelper;
import baecon.devgames.events.SynchronizableModelUpdatedEvent;
import baecon.devgames.events.UsersUpdatedEvent;
import baecon.devgames.model.Project;
import baecon.devgames.model.User;
import baecon.devgames.model.update.UserUpdate;
import baecon.devgames.util.Utils;

/**
 * Created by Marcel on 16-3-2016.
 */
public class PollRelatedUsersTask extends ModelPollTask<User, UserUpdate, UserDTO> {
    private Long projectId;

    public PollRelatedUsersTask(Context context, AbsModelManager modelManager) {
        super(context, modelManager);
    }

    public PollRelatedUsersTask(Context context, AbsModelManager modelManager, Long projectId) {
        super(context, modelManager);
        this.projectId = projectId;
    }

    @Override
    protected List<UserDTO> doPoll(DevGamesClient client) {
        return client.getUsers(projectId);
    }

    @Override
    protected User dtoToModel(UserDTO dto) {
        if (dto != null) return dto.toModel();
        else return null;
    }

    @Override
    protected Dao<User, Long> getModelDao() {
        return DBHelper.getUserDao(getDbHelper());
    }

    @Override
    protected Dao<UserUpdate, Long> getModelUpdateDao() {
        return DBHelper.getUserUpdateDao(getDbHelper());
    }

    @Override
    protected SynchronizableModelUpdatedEvent getUpdatedEvent(Integer result, HashSet<Long> removed, HashSet<Long> added, HashSet<Long> updated) {
        return new UsersUpdatedEvent(result, removed, added, updated);
    }
}
