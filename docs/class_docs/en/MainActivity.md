# MainActivity Class Documentation

## 1. General Information
- **Class Name:** `MainActivity`
- **Type:** Activity
- **Purpose:** This class serves as the main control panel for the application. It is responsible for displaying the user interface, handling button clicks to start different background tasks, and showing logs to the user so they can see what is happening in real-time.
- **Interaction:** It interacts with `MusicPlayerService` (to start/stop music), `TimerService` (to get timer values), `WorkManager` (to schedule tasks), and `MyAsyncTask` (to demonstrate legacy background work).

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where is it used |
| :--- | :--- | :--- | :--- |
| `logTextView` | `TextView` | Displays messages to the user about what the app is doing. | Used in `log()` method to show text on screen. |
| `asyncProgressBar` | `ProgressBar` | Shows progress for the `AsyncTask` demonstration. | Passed to `MyAsyncTask` constructor. |
| `mainHandler` | `Handler` | A tool to send messages or tasks to the main (UI) thread. | Used to safely update the UI from background threads. |
| `singleThreadExecutor`| `ExecutorService`| A managed thread that executes tasks one by one. | Used in "Module 1" to run background tasks sequentially. |
| `fixedThreadPool` | `ExecutorService`| A pool of 4 threads that can run tasks at the same time. | Used in "Module 1" to run tasks concurrently. |
| `timerService` | `TimerService` | Holds a reference to the bound service. | Used to get the current timer value. |
| `isBound` | `boolean` | Tracks if the Activity is currently connected to `TimerService`. | Checked before trying to use `timerService`. |
| `periodicWorkId` | `UUID` | Stores the unique ID of the periodic work request. | Used to cancel the periodic work when requested. |

## 3. Class Methods

### Method Name: `onCreate`
- **Type:** `protected`
- **Return value:** `void` (nothing)
- **Parameters:** `Bundle savedInstanceState` (Saved data from previous session)
- **What it does:** 
    1. Sets the layout for the screen (`setContentView`).
    2. Initializes views (finding buttons and text by ID).
    3. Sets up background executors (thread pools).
    4. Sets up button listeners and the service connection.
- **When called:** Automatically by Android when the app starts.
- **What is important:** This is the entry point. If you do too much work here, the app will start slowly.

### Method Name: `log`
- **Type:** `public`
- **Return value:** `void`
- **Parameters:** `String message` (The text to display)
- **What it does:** 
    1. Gets the name of the current thread.
    2. Uses `runOnUiThread` to safely add the message to `logTextView`.
- **When called:** Manually whenever we want to show progress.
- **What is important:** It must be thread-safe because it's called from many background threads.

### Method Name: `handleStartService`
- **Type:** `private`
- **Return value:** `void`
- **What it does:** Checks for notification permissions (required on Android 13+) before starting the music service.
- **When called:** When the "Start Foreground Service" button is clicked.

## 4. Lifecycle (Activity Only)
- **`onCreate()`:** Called when the Activity is created. Initializes everything.
- **`onDestroy()`:** Called before the Activity is closed. Important: It shuts down thread pools and unbinds services to prevent the app from wasting battery or crashing.

## 5. Interface Interaction (UI)
- **Elements:** Buttons (`R.id.freeze_ui_button`, etc.), `TextView` for logs, `ProgressBar`.
- **Connection:** Uses `findViewById(R.id.id_name)` to link XML elements to Java variables.
- **Events:** Handles `setOnClickListener` for many buttons to trigger background demonstrations.

## 6. Interaction with other components
- **Services:** Uses `startService` and `bindService` to talk to background services.
- **WorkManager:** Uses `WorkManager.getInstance().enqueue()` to schedule tasks like file downloads.
- **AsyncTask:** Creates a new instance of `MyAsyncTask` and calls `execute()`.

## 7. General Logic
The class acts as a coordinator. When a user clicks a button, `MainActivity` decides which background mechanism to use (Thread, Executor, Service, or WorkManager). It then listens for updates and displays them in the log on the screen.

**Use Case:**
1. User clicks "Run in Thread Pool".
2. `MainActivity` loops 10 times and submits tasks to `fixedThreadPool`.
3. Background threads wait, then tell `MainActivity` they are done.
4. `MainActivity` updates the log on the screen.

## 8. Simplified Explanation
Imagine `MainActivity` is the **Manager of a Restaurant**. 
- The **UI (Screen)** is the front of the house where customers (User) sit.
- The **Background Tasks** are the kitchen staff.
- The Manager (MainActivity) takes orders from the customer and gives them to the chefs.
- To keep the front of the house happy, the Manager doesn't do the cooking himself (doesn't block the UI thread). Instead, he watches the kitchen and tells the customer when the food is ready.

---
**Tip for Students:** Notice how `onDestroy` is used to "clean up". In Android, forgetting to stop background work when an Activity closes is like leaving the stove on after the restaurant is closed—it's dangerous and wasteful!
