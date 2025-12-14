package zeev.fraiman.backgroundlab;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The main screen of the application, serving as a control panel for all background task demonstrations.
 */
public class MainActivity extends AppCompatActivity {

    private TextView logTextView;
    private ProgressBar asyncProgressBar;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService singleThreadExecutor;
    private ExecutorService fixedThreadPool;

    // Bound Service
    private TimerService timerService;
    private boolean isBound = false;
    private ServiceConnection serviceConnection;

    // WorkManager
    private UUID periodicWorkId;

    /**
     * Handles the result of the notification permission request.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    log("Notification permission granted. Starting service...");
                    startMusicService();
                } else {
                    log("Notification permission denied.");
                    Toast.makeText(this, "Permission needed for notifications", Toast.LENGTH_SHORT).show();
                }
            }
    );

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up:
     * create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's
     * previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initBackgroundExecutors();
        setupButtons();
        setupServiceConnection();
    }

    /**
     * Initializes all the views used in this activity by finding them by their ID in the layout.
     */
    private void initViews() {
        logTextView = findViewById(R.id.log_text_view);
        asyncProgressBar = findViewById(R.id.async_progress_bar);
    }

    /**
     * Initializes the ExecutorServices used for demonstrating Java Core threading.
     */
    private void initBackgroundExecutors() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        fixedThreadPool = Executors.newFixedThreadPool(4);
    }

    /**
     * A helper method to orchestrate the setup of all button listeners in the activity.
     */
    private void setupButtons() {
        setupModule1Buttons();
        setupModule2Buttons();
        setupModule3Buttons();
        setupModule4Buttons();
        setupInfoButtons();
    }

    /**
     * Sets up OnClickListeners for all buttons related to Module 1: Java Core Threads.
     * This includes demonstrations of UI freezing, basic Threads, Handlers, and ExecutorServices.
     */
    private void setupModule1Buttons() {
        findViewById(R.id.freeze_ui_button).setOnClickListener(v -> {
            log("Starting heavy work on UI thread...");
            SystemClock.sleep(8000);
            log("Heavy work on UI thread finished. ANR might have occurred.");
        });

        findViewById(R.id.start_in_thread_button).setOnClickListener(v -> {
            log("Starting new Thread...");
            new Thread(() -> {
                log("Work started in new Thread.");
                SystemClock.sleep(2000);
                log("Work finished. Direct UI update would crash!");
            }).start();
        });

        findViewById(R.id.start_with_handler_button).setOnClickListener(v -> {
            log("Starting new Thread with Handler...");
            new Thread(() -> {
                log("Work started in background.");
                SystemClock.sleep(2000);
                log("Work finished. Posting to Handler.");
                mainHandler.post(() -> log("Handler running on main thread. Success!"));
            }).start();
        });

        findViewById(R.id.run_in_single_executor_button).setOnClickListener(v -> {
            log("Submitting 10 tasks to SingleThreadExecutor...");
            for (int i = 1; i <= 10; i++) {
                final int taskNumber = i;
                singleThreadExecutor.submit(() -> {
                    log("Task " + taskNumber + " starting.");
                    SystemClock.sleep(1000);
                    log("Task " + taskNumber + " finished.");
                });
            }
        });

        findViewById(R.id.run_in_thread_pool_button).setOnClickListener(v -> {
            log("Submitting 10 tasks to FixedThreadPool(4)...");
            for (int i = 1; i <= 10; i++) {
                final int taskNumber = i;
                fixedThreadPool.submit(() -> {
                    log("Task " + taskNumber + " starting.");
                    SystemClock.sleep(1000);
                    log("Task " + taskNumber + " finished.");
                });
            }
        });
    }

    /**
     * Sets up OnClickListeners for all buttons related to Module 2: Android Services.
     * This includes starting/stopping a Foreground Service and binding/unbinding/interacting with a Bound Service.
     */
    private void setupModule2Buttons() {
        findViewById(R.id.start_foreground_service_button).setOnClickListener(v -> handleStartService());
        findViewById(R.id.stop_foreground_service_button).setOnClickListener(v -> {
            log("Stopping Foreground Service...");
            stopService(new Intent(this, MusicPlayerService.class));
        });

        findViewById(R.id.bind_service_button).setOnClickListener(v -> {
            if (!isBound) {
                log("Binding to TimerService...");
                bindService(new Intent(this, TimerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                log("Already bound to TimerService.");
            }
        });

        findViewById(R.id.get_timer_button).setOnClickListener(v -> {
            if (isBound) {
                log("Timer value from service: " + timerService.getTimerValue());
            } else {
                log("Cannot get timer value. Service not bound.");
            }
        });

        findViewById(R.id.unbind_service_button).setOnClickListener(v -> {
            if (isBound) {
                log("Unbinding from TimerService...");
                unbindService(serviceConnection);
                isBound = false;
            } else {
                log("Service is not bound.");
            }
        });
    }

    /**
     * Sets up OnClickListeners for all buttons related to Module 3: WorkManager.
     * This includes enqueuing one-time, periodic, constrained, and chained work requests.
     */
    private void setupModule3Buttons() {
        findViewById(R.id.one_time_work_button).setOnClickListener(v -> {
            log("Enqueuing OneTimeWorkRequest for PhotoCompressionWorker.");
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(PhotoCompressionWorker.class).build();
            WorkManager.getInstance(getApplicationContext()).enqueue(request);
            observeWork(request.getId(), "OneTime");
        });

        findViewById(R.id.periodic_work_button).setOnClickListener(v -> {
            if (periodicWorkId != null) {
                log("Periodic work is already running.");
                return;
            }
            log("Enqueuing PeriodicWorkRequest (repeats every 15 min).");
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(SyncWorker.class, 15, TimeUnit.MINUTES).build();
            periodicWorkId = request.getId();
            WorkManager.getInstance(getApplicationContext()).enqueue(request);
            observeWork(periodicWorkId, "Periodic");
        });

        findViewById(R.id.cancel_periodic_work_button).setOnClickListener(v -> {
            if (periodicWorkId != null) {
                log("Cancelling periodic work with ID: " + periodicWorkId);
                WorkManager.getInstance(getApplicationContext()).cancelWorkById(periodicWorkId);
                periodicWorkId = null;
            } else {
                log("No periodic work is scheduled.");
            }
        });

        findViewById(R.id.constrained_work_button).setOnClickListener(v -> {
            log("Enqueuing Work with constraints (Charging, Wi-Fi).");
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).setRequiresCharging(true).build();
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadWorker.class).setConstraints(constraints).build();
            WorkManager.getInstance(getApplicationContext()).enqueue(request);
            observeWork(request.getId(), "Constrained");
        });

        findViewById(R.id.chain_work_button).setOnClickListener(v -> {
            log("Enqueuing a chain of work: Download -> Unpack -> Save");
            OneTimeWorkRequest downloadWork = new OneTimeWorkRequest.Builder(DownloadWorker.class).build();
            OneTimeWorkRequest unpackWork = new OneTimeWorkRequest.Builder(UnpackWorker.class).build();
            OneTimeWorkRequest saveWork = new OneTimeWorkRequest.Builder(SaveToDbWorker.class).build();
            WorkManager.getInstance(getApplicationContext()).beginWith(downloadWork).then(unpackWork).then(saveWork).enqueue();
            observeWork(downloadWork.getId(), "Chain-Download");
            observeWork(unpackWork.getId(), "Chain-Unpack");
            observeWork(saveWork.getId(), "Chain-Save");
        });
    }

    /**
     * Sets up OnClickListeners for all buttons related to Module 4: Legacy & Deprecated APIs.
     * This includes a demonstration of AsyncTask.
     */
    private void setupModule4Buttons() {
        findViewById(R.id.run_asynctask_button).setOnClickListener(v -> {
            log("Starting deprecated AsyncTask...");
            log("Rotate the screen to see the potential memory leak issue!");
            new MyAsyncTask(this, asyncProgressBar).execute();
        });
    }

    /**
     * Sets up OnClickListeners for the information icons next to each module title.
     * Clicking an icon will display an AlertDialog with an explanation of that module.
     */
    private void setupInfoButtons() {
        findViewById(R.id.info_module1).setOnClickListener(v -> showInfoDialog("Module 1: Java Core Threads", "This module demonstrates the basics of multithreading in Java.\n\n- Freeze UI: Shows what happens when you run a heavy task on the main (UI) thread.\n- New Thread: Moves the heavy task to a background thread. The UI no longer freezes, but trying to update it from the background will crash the app.\n- Handler: Shows the classic way to safely post updates from a background thread to the UI thread.\n- Executors: Demonstrates modern, managed thread pools for running multiple tasks concurrently or sequentially."));

        findViewById(R.id.info_module2).setOnClickListener(v -> showInfoDialog("Module 2: Android Services", "Services are for background tasks that should live longer than the UI.\n\n- Foreground Service: For long-running, user-aware tasks like a music player. It MUST show a persistent notification. It will keep running even if the app is closed.\n- Bound Service: Provides a client-server interface within the app. The service lives only as long as components are bound to it. Used for getting data from a background process (like a timer)."));

        findViewById(R.id.info_module3).setOnClickListener(v -> showInfoDialog("Module 3: WorkManager", "WorkManager is the modern, recommended library for guaranteed, battery-friendly background work.\n\n- One-Time: A simple, single-run task.\n- Periodic: A task that repeats at a specified interval (min 15 minutes).\n- Constraints: Deferrable tasks that run only when conditions (e.g., charging, Wi-Fi) are met. This is the key to battery efficiency!\n- Chain: Link multiple tasks to run in a specific sequence."));

        findViewById(R.id.info_module4).setOnClickListener(v -> showInfoDialog("Module 4: Legacy & Deprecated", "This module shows older APIs that you shouldn\'t use in new code but might encounter in old projects.\n\n- AsyncTask: The infamous class for short background tasks. It\'s deprecated due to design flaws, most notably causing memory leaks when the screen is rotated during its operation."));
    }

    /**
     * Displays an AlertDialog with a given title and message.
     *
     * @param title   The title to display in the dialog.
     * @param message The main content message to display in the dialog.
     */
    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Got it", null)
                .show();
    }

    /**
     * Initializes the ServiceConnection object used to manage the lifecycle of the connection to the TimerService.
     */
    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
                timerService = binder.getService();
                isBound = true;
                log("TimerService connected.");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
                timerService = null;
                log("TimerService disconnected.");
            }
        };
    }

    /**
     * Observes a WorkManager WorkRequest by its ID and logs its status changes to the in-app console.
     *
     * @param id       The UUID of the work to observe.
     * @param workName A descriptive name for the work for logging purposes.
     */
    private void observeWork(UUID id, String workName) {
        WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(id).observe(this, workInfo -> {
            if (workInfo != null) {
                log(workName + " Status: " + workInfo.getState());
                if (workInfo.getState() == WorkInfo.State.BLOCKED) {
                    log(workName + " is BLOCKED. Waiting for constraints to be met.");
                }
            }
        });
    }

    /**
     * Handles the business logic for starting the MusicPlayerService.
     * It checks for notification permissions on Android 13+ before starting the service.
     */
    private void handleStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        startMusicService();
    }

    /**
     * Starts the MusicPlayerService using the appropriate method for the device's Android version.
     * (startForegroundService for Android O+).
     */
    private void startMusicService() {
        log("Starting MusicPlayerService...");
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    /**
     * A thread-safe method to log messages to the on-screen TextView.
     * It prepends the message with the name of the thread it originated from.
     *
     * @param message The message to log.
     */
    public void log(final String message) {
        final String threadName = Thread.currentThread().getName();
        runOnUiThread(() -> logTextView.append("\n[" + threadName + "] " + message));
    }

    /**
     * Called when the activity is about to be destroyed. This is the final call that the activity receives.
     * It is used here to clean up resources, such as unbinding services and shutting down thread pools.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy called.");
        if (isBound) {
            unbindService(serviceConnection);
        }
        if (singleThreadExecutor != null) singleThreadExecutor.shutdownNow();
        if (fixedThreadPool != null) fixedThreadPool.shutdownNow();
        log("Executors shut down.");
    }
}
