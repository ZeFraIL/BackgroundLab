package zeev.fraiman.backgroundlab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * A demonstration of a Foreground Service.
 * This service simulates a music player that continues to run in the background even when the app is closed.
 * It shows a persistent notification, which is a requirement for all foreground services.
 */
public class MusicPlayerService extends Service {

    private static final String TAG = "MusicPlayerService";
    public static final String CHANNEL_ID = "MusicPlayerServiceChannel";
    private volatile boolean isRunning = false;
    private Thread workerThread;

    /**
     * Called by the system when the service is first created.
     * Used for one-time setup procedures.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        createNotificationChannel();
    }

    /**
     * Called by the system every time a client starts the service by calling startService(Intent).
     * This method is where the service's main logic resides.
     *
     * @param intent The Intent supplied to startService(Intent).
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Playing some background tunes...")
                .setSmallIcon(R.mipmap.ic_launcher) // Replace with a real icon
                .build();

        // Promotes the service to a foreground service, showing the notification.
        startForeground(1, notification);

        if (!isRunning) {
            isRunning = true;
            workerThread = new Thread(() -> {
                int count = 0;
                while (isRunning) {
                    try {
                        Log.i(TAG, "Music playing... track time: " + count++ + "s");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Log.w(TAG, "Worker thread interrupted.");
                    }
                }
                Log.i(TAG, "Music has stopped.");
            });
            workerThread.start();
        }

        // If the service is killed, it will be automatically restarted.
        return START_STICKY;
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     * Used for cleanup of any resources like threads, registered listeners, etc.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
        isRunning = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    /**
     * Return the communication channel to the service.
     * This service does not support binding, so we return null.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return Return an IBinder through which clients can call on to the service. Return null if clients cannot bind.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates a NotificationChannel, required for showing notifications on Android 8.0 (API 26) and higher.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
