package zeev.fraiman.backgroundlab;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

/**
 * A demonstration of the deprecated AsyncTask.
 * This implementation uses WeakReference to mitigate (but not completely solve)
 * the classic memory leak issue where the AsyncTask holds a reference to the Activity,
 * preventing it from being garbage collected on configuration changes (e.g., screen rotation).
 */
@SuppressWarnings("deprecation")
public class MyAsyncTask extends AsyncTask<Void, Integer, String> {

    private final WeakReference<MainActivity> activityReference;
    private final WeakReference<ProgressBar> progressBarReference;

    /**
     * Constructor.
     * @param activity The MainActivity instance. A WeakReference is stored to avoid memory leaks.
     * @param progressBar The ProgressBar to update. A WeakReference is stored.
     */
    public MyAsyncTask(MainActivity activity, ProgressBar progressBar) {
        this.activityReference = new WeakReference<>(activity);
        this.progressBarReference = new WeakReference<>(progressBar);
    }

    /**
     * Runs on the UI thread before doInBackground().
     * Used here to make the ProgressBar visible and log the start of the task.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        ProgressBar progressBar = progressBarReference.get();
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
        activity.log("AsyncTask: onPreExecute");
    }

    /**
     * Performs the long-running operation on a background thread.
     * @param voids The parameters of the task (not used here).
     * @return A string result indicating the task is finished.
     */
    @Override
    protected String doInBackground(Void... voids) {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            activity.log("AsyncTask: doInBackground starting...");
        }

        for (int i = 0; i <= 100; i++) {
            if (isCancelled()) {
                return "Cancelled";
            }
            SystemClock.sleep(50); // Simulate work
            publishProgress(i);
        }
        return "Task Finished!";
    }

    /**
     * Runs on the UI thread after publishProgress() is invoked.
     * Used here to update the ProgressBar.
     * @param values The progress values.
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        ProgressBar progressBar = progressBarReference.get();
        if (progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }

    /**
     * Runs on the UI thread after doInBackground() has finished.
     * @param result The result of the operation.
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        activity.log("AsyncTask: onPostExecute - " + result);
        ProgressBar progressBar = progressBarReference.get();
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
