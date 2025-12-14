package zeev.fraiman.backgroundlab;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker that simulates a long-running task of compressing a photo.
 * This is used to demonstrate a simple one-time background task with WorkManager.
 */
public class PhotoCompressionWorker extends Worker {

    private static final String TAG = "PhotoCompressionWorker";

    public PhotoCompressionWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * The main entry point for the background work. This method is called on a background thread.
     * It simulates a photo compression task by sleeping and logging progress.
     *
     * @return The result of the work, either {@link Result#success()} or {@link Result#failure()}.
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork: Starting photo compression...");
        try {
            // Simulate long-running work
            for (int i = 0; i < 100; i += 20) {
                Thread.sleep(500);
                Log.i(TAG, "Compression progress: " + i + "%");
            }
            Log.i(TAG, "doWork: Photo compression finished successfully.");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "doWork: Photo compression failed.", e);
            return Result.failure();
        }
    }
}
