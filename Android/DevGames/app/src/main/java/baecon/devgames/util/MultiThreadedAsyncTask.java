package baecon.devgames.util;

import android.os.AsyncTask;

public abstract class MultiThreadedAsyncTask<P, I, R> extends AsyncTask<P, I, R> {

    /**
     * Since pre-honeycomb SDK's only allow for a small amount of
     * simultaneous executed AsyncTasks, this method checks if we're
     * running on a more modern phone (SDK 11+), and if this is the
     * case, execute this task multi-threaded instead of serial.
     *
     * @param params
     *         The parameters of this task.
     */
    public final AsyncTask<P, I, R> executeThreaded(P... params) {
        return super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
}
