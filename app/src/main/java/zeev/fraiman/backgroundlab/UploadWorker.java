package zeev.fraiman.backgroundlab;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker that simulates a file upload task.
 * This is used to demonstrate a WorkRequest with Constraints (e.g., requires Wi-Fi and charging).
 */
public class UploadWorker extends Worker {

    private static final String TAG = "UploadWorker";

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * The main entry point for the background work. This method is called on a background thread.
     * It simulates a network upload operation.
     *
     * @return The result of the work, always {@link Result#success()} in this simulation.
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork: Starting file upload... (Pretending to connect to server)");
        try {
            // Simulate a network operation
            Thread.sleep(3000);
            Log.i(TAG, "doWork: File upload completed successfully.");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "doWork: File upload was interrupted.", e);
            return Result.failure();
        }
    }
}
