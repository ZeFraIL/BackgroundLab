package zeev.fraiman.backgroundlab;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * The final step in a work chain. This Worker simulates saving data to a database.
 * It is used to demonstrate sequential work with WorkManager.
 */
public class SaveToDbWorker extends Worker {

    private static final String TAG = "SaveToDbWorker";

    public SaveToDbWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * The main entry point for the background work. This method is called on a background thread.
     * Simulates a database write operation.
     *
     * @return The result of the work.
     */
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Starting to save data to database...");
        try {
            Thread.sleep(1000);
            Log.i(TAG, "Data saved successfully.");
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Saving to DB failed.", e);
            return Result.failure();
        }
    }
}
