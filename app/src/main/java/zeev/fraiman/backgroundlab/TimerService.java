package zeev.fraiman.backgroundlab;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A demonstration of a Bound Service.
 * This service maintains a simple counter and allows clients (like MainActivity) to bind to it
 * and retrieve the current counter value. The service lives only as long as there is at least
 * one client bound to it.
 */
public class TimerService extends Service {

    private static final String TAG = "TimerService";
    private final IBinder binder = new TimerBinder();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile int timerValue = 0;
    private volatile boolean isRunning = false;

    /**
     * The Binder implementation that provides clients access to this service instance.
     */
    public class TimerBinder extends Binder {
        TimerService getService() {
            // Return this instance of TimerService so clients can call public methods
            return TimerService.this;
        }
    }

    /**
     * Called by the system when the service is first created.
     * Starts a background thread to increment the timer.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created.");
        isRunning = true;
        executor.submit(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(1000);
                    timerValue++;
                    Log.d(TAG, "Timer ticked: " + timerValue);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    /**
     * Called by the system when a client binds to the service using bindService().
     *
     * @param intent The Intent that was used to bind to this service.
     * @return The IBinder through which clients can communicate with the service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Client bound to service.");
        return binder;
    }

    /**
     * Called when all clients have unbound from a particular interface published by the service.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return The default implementation returns false.
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Client unbound from service.");
        // The service will be destroyed automatically as we return super.onUnbind()
        return super.onUnbind(intent);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     * Cleans up resources, such as stopping the timer thread.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        executor.shutdownNow();
        Log.d(TAG, "onDestroy: Service destroyed.");
    }

    /**
     * Public method for clients to get the current timer value.
     * @return The current value of the timer.
     */
    public int getTimerValue() {
        return timerValue;
    }
}
