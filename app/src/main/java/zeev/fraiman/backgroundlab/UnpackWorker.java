package zeev.fraiman.backgroundlab;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * The second step in a work chain. This Worker simulates unpacking a file that was previously downloaded.
 * It is used to demonstrate sequential work with WorkManager.
 */
public class UnpackWorker extends Worker {

    private static final String TAG = "UnpackWorker";

    public UnpackWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * The main entry point for the background work. This method is called on a background thread.
     * Simulates a file unpacking operation.
     *
     * @return The result of the work.
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Starting file unpacking...");
        try {
            Thread.sleep(1500);
            Log.i(TAG, "File unpacked successfully.");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Unpacking failed.", e);
            return Result.failure();
        }
    }
}
