package baecon.devgames.connection.task.poll;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import baecon.devgames.connection.task.RESTTask;
import baecon.devgames.database.DBHelper;
import baecon.devgames.connection.client.dto.UserDTO;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.User;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import retrofit.RetrofitError;

public class PollUserTask extends RESTTask<Void, Void, Integer> {

    private Long userId;
    private Long userLocalId;

    public PollUserTask(Context context, Long userId) {
        super(context);
        this.userId = userId;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        if (!Utils.hasInternetConnection(getApplication())) {
            return null;
        }

        UserDTO dto;
        try {
            dto = createService().getUser(userId);
        } catch (RetrofitError error) {
            int status = super.getStatus(error);

            // Also check if the user is still logged in
            // Otherwise this check will login again, that is not a good idea :)
            if ((status == FORBIDDEN || status == UNAUTHORIZED) && getLoggedInUser() != null) {
                super.requestReLogin();
                L.d("user was requested to re-login: {0}", getLoggedInUser().getId());
            }

            L.e(error, "HTTP error: {0}", status);
            return status;
        } catch (Exception e) {
            L.e(e, "something unexpected happened");
            return RESTTask.GENERAL_CONNECTION_ERROR;
        }

        L.v("Retrieved user {0}", dto);

        if (dto == null) {

            // Apparently, User does not exist, or we do not have sufficient rights to see the user
            return RESTTask.FORBIDDEN;
        }

        Dao<User, Long> userDao = DBHelper.getUserDao(getDbHelper());
        try {

            User user = userDao.queryBuilder()
                    .where()
                    .eq(ISynchronizable.Column.ID, userId)
                    .queryForFirst();

            L.v("User was {0} in local database", user == null ? "not yet" : "already");

            userLocalId = user != null ? user.getId() : 0l;

            user = dto.toModel();

            L.v("Retrieved user is in team {0}", user.getUsername());

            user.setId(userLocalId);

            userDao.createOrUpdate(user);

            L.v("User created/updated in local database {0}", user);

            userLocalId = user.getId();

            return OK;
        } catch (SQLException e) {
            L.e(e, "Could not save user in database");
            return null;
        }
    }
}
