package zeev.fraiman.backgroundlab;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker that simulates a periodic data synchronization task.
 * This is used to demonstrate a PeriodicWorkRequest with WorkManager.
 */
public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * The main entry point for the background work. This method is called on a background thread.
     * It simulates a short data sync operation.
     *
     * @return The result of the work, always {@link Result#success()} in this simulation.
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork: Starting periodic data sync...");
        try {
            // Simulate some work
            Thread.sleep(1500);
            Log.i(TAG, "doWork: Data sync finished successfully.");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "doWork: Data sync failed.", e);
            return Result.failure();
        }
    }
}
