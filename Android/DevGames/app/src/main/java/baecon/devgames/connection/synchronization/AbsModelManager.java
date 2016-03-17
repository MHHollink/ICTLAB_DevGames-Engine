package baecon.devgames.connection.synchronization;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.connection.task.poll.ModelPollTask;
import baecon.devgames.connection.task.push.ModelPushTask;
import baecon.devgames.connection.client.dto.ModelDTO;
import baecon.devgames.events.PushTaskDoneEvent;
import baecon.devgames.model.AbsSynchronizable;
import baecon.devgames.database.modelupdate.IModelUpdate;
import baecon.devgames.model.update.AbsModelUpdate;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public abstract class AbsModelManager
        <
                Model extends AbsSynchronizable,
                DTOClass extends ModelDTO,
                ModelUpdateClass extends AbsModelUpdate,
                PushTaskDoneEventClass extends PushTaskDoneEvent
                >
        implements IModelManager<Model> {

    private DevGamesApplication app;

    private boolean inited;

    private boolean scheduledPollingEnabled = false;

    private boolean inForegroundSyncMode = false;

    /**
     * The async tasks that run to synchronize the {@link IModelUpdate}s. There's 1 async task available per model
     * instance. The key is the {@link IModelUpdate#getId()}
     */
    private HashMap<Long, ModelPushTask<Model, ModelUpdateClass>> modelUpdateTasks;

    private Handler handler;

    protected ModelPollTask<Model, ModelUpdateClass, DTOClass> pollTask;

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            startPoll();
            L.d("Running {0}", pollTask.getClass().getSimpleName());

            if (isInForegroundSyncMode()) {
                handler.postDelayed(this, getForegroundPollingInterval());
            } else {
                if (isAllowedToSyncInBackground()) {
                    handler.postDelayed(this, getBackgroundPollingInterval());
                }
            }
        }
    };

    protected AbsModelManager(DevGamesApplication app) {
        this.app = app;
        handler = new Handler(Looper.getMainLooper());
    }

    protected Handler getUIThreadHandler(){
        return handler;
    }

    @Override
    public void offerUpdate(final Long id) {
        L.v("{0}", id);

        handler.post(new Runnable() {
            public void run() {
                // And fire up the AsyncTask to push it to the back-end
                launchAsyncTask(id);
            }
        });
    }

    @Override
    public void offerUpdate(IModelUpdate modelUpdate) {
        L.v("{0}", modelUpdate);

        // And fire up the AsyncTask to push it to the back-end
        launchAsyncTask(modelUpdate.getId());
    }

    /**
     * Starts a new async task to poll the back-end for new data. If needed, a new task is created using {@link
     * #newPollTask(android.content.Context)}.
     */
    @Override
    public void startPoll() {

        // A task that is finished, cannot be reused, so we'll have to create a new one in order to execute the poll
        if (pollTask == null || pollTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            L.v("Poll task was null or Status.FINISHED, creating new one");
            pollTask = newPollTask(app);
        }

        if (pollTask == null) {
            // It would be silly to let the app crash, because a developer is lazy.
            // So, kick his butt with these logs
            L.wtf("Hey developer! Don't feed me a RESTTask that is null!");
            L.wtf("Refusing to continue until a not null RESTTask is given!");
            return;
        }

        /**
         * Check the current status of the task:
         * - Finished: call this function again, so it will create a new task and execute that
         * - Pending: task is not executed yet, execute it
         * - Running: task is busy. Since we only run one task at a time, do nothing and log a message about this behaviour
         */
        switch (pollTask.getStatus()) {

            case FINISHED:
                L.v("Poll task finished, calling self again to create new task.");
                startPoll();
                break;
            case PENDING:
                L.v("Poll task standby, start to execute task.");
                pollTask.executeOnExecutor(getPollExecutor());
                break;
            case RUNNING:
                L.v("Poll task already running! No new task created.");
                break;
        }
    }

    protected Executor getPollExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    public void schedulePolling() {

        scheduledPollingEnabled = true;

        // Create a runnable that will be scheduled every time after it ran on the handler
        // And execute the very first poll directly
        handler.removeCallbacks(pollRunnable);
        handler.post(pollRunnable);
    }

    public void stopSchedulePolling() {
        L.i("Stopped scheduled polling");
        handler.removeCallbacks(pollRunnable);

        scheduledPollingEnabled = false;
    }

    public boolean scheduledPollingEnabled() {
        return scheduledPollingEnabled;
    }

    /**
     * Launches an AsyncTask that takes care of synchronization to the back-end. Only one task per model instance is
     * executed. If the task is already running, the task will pick-up the {@link IModelUpdate} in its while loop.
     *
     * @param id
     *         The local id of the model
     */
    protected void launchAsyncTask(long id) {
        L.v("{1}, localModelId {0}", id, getClass().getSimpleName());

        // TODO: check if local model id is > 0

        if (modelUpdateTasks == null) {
            modelUpdateTasks = new HashMap<>();
        }

        ModelPushTask<Model, ModelUpdateClass> syncTask = modelUpdateTasks.get(id);

        if (syncTask == null || syncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            L.d("syncTask was null or getStatus==FINISHED. Creating new ModelUpdateTask");
            syncTask = newUpdateTask(app, id);
            modelUpdateTasks.put(id, syncTask);
            return;
        }

        switch (syncTask.getStatus()) {
            case FINISHED:
                L.wtf("finished: that should not be possible..");
                break;
            case PENDING:
                L.d("pending: syncTask.executeThreaded");
                syncTask.executeThreaded();
                break;
            case RUNNING:
                L.d("Update sync task already running");
                break;
            default:
                throw new RuntimeException("Unhandled case");
        }
    }

    protected void onUpdateTaskDoneEvent(PushTaskDoneEventClass event) {
        L.v("event={0}", event);

        // This event also arrives at the correct UI, and that will update accordingly on its own
        // For now, we do nothing with this information

        if (!event.success) {
            // Some update failed
            L.d("Update failed, will be retried in next sync cycle");
        }
    }

    /**
     * Starts a one shot poll.
     */
    public void poke() {

        L.v("*yawn*, pass me some coffee! Let's see how much work their is to do");

        startPoll();
    }

    @Override
    public void init() {
        inited = true;
    }

    public boolean isInited() {
        return inited;
    }

    @Override
    public void shutdown() {
        L.d("Goodbye world!");
        stopSchedulePolling();
        if (modelUpdateTasks != null) {
            for (Map.Entry<Long, ModelPushTask<Model, ModelUpdateClass>> entry : modelUpdateTasks.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().cancel(true);
                }
            }
        }
        modelUpdateTasks = null;
        pollTask = null;

        inited = false;
    }

    public void startUpdateTasks(List<Long> localModelIds) {

        L.v("localModelIds={0}", Utils.collectionToString(localModelIds));

        if (localModelIds == null) {
            L.d("No local model ids given for waiting updates to the back-end");
        }
        else {
            for (Long localModelId : localModelIds) {
                if (localModelId != null) {
                    launchAsyncTask(localModelId);
                }
                else {
                    L.w("A local model id that was flagged for a waiting update was null!");
                }
            }
        }
    }

    @Override
    public void startForegroundSyncMode() {
        inForegroundSyncMode = true;
        schedulePolling();
    }

    @Override
    public void stopForegroundSyncMode() {
        inForegroundSyncMode = false;
    }

    @Override
    public boolean isInForegroundSyncMode() {
        return inForegroundSyncMode;
    }

    @Override
    public DevGamesApplication getApplication() {
        return app;
    }

    protected abstract ModelPushTask<Model, ModelUpdateClass> newUpdateTask(DevGamesApplication app, Long id);

    protected abstract ModelPollTask<Model, ModelUpdateClass, DTOClass> newPollTask(Context context);

//    public abstract void create(Model model);
//
//    public abstract void update(Model model);
//
//    public abstract void update(long localModelId, String field, Serializable value);
//
//    public abstract void delete(Model model);
}