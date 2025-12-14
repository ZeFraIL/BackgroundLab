package zeev.fraiman.backgroundlab;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * The first step in a work chain. This Worker simulates downloading a file.
 * It is used to demonstrate sequential work with WorkManager.
 */
public class DownloadWorker extends Worker {

    private static final String TAG = "DownloadWorker";

    public DownloadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * The main entry point for the background work. This method is called on a background thread.
     * Simulates a file download operation.
     *
     * @return The result of the work.
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Starting file download...");
        try {
            Thread.sleep(2000);
            Log.i(TAG, "File downloaded successfully.");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Download failed.", e);
            return Result.failure();
        }
    }
}
