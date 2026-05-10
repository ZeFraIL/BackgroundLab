# TimerService Class Documentation

## 1. General Information
- **Class Name:** `TimerService`
- **Type:** Service (Bound Service)
- **Purpose:** This class demonstrates a "Bound Service." It maintains a simple timer that counts seconds in the background. Unlike a foreground service, this service is designed to communicate directly with an Activity (like a client-server relationship).
- **Interaction:** `MainActivity` binds to this service to get the current timer value. The service stays alive as long as `MainActivity` is connected to it.

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where is it used |
| :--- | :--- | :--- | :--- |
| `binder` | `IBinder` | An interface that allows the Activity to communicate with the Service. | Returned in `onBind()`. |
| `executor` | `ExecutorService` | A managed thread used to run the timer loop in the background. | Started in `onCreate()`. |
| `timerValue` | `int` | Holds the current number of elapsed seconds. | Incremented in the loop; returned by `getTimerValue()`. |
| `isRunning` | `boolean` | Controls the execution of the background timer loop. | Checked in the `while` loop condition. |

## 3. Class Methods

### Method Name: `onCreate`
- **Type:** `public`
- **Return value:** `void`
- **What it does:** 
    1. Initializes the timer and starts the background thread.
    2. The thread sleeps for 1 second, increments `timerValue`, and logs it.
- **When called:** When the service is first created by the system.

### Method Name: `onBind`
- **Type:** `public`
- **Return value:** `IBinder`
- **Parameters:** `Intent intent`
- **What it does:** Returns the `binder` object. This is like giving the Activity a "key" to access the service's methods.
- **When called:** When `MainActivity` calls `bindService()`.

### Method Name: `getTimerValue` (Public Method)
- **Type:** `public`
- **Return value:** `int` (Current timer count)
- **What it does:** Simply returns the current value of `timerValue`.
- **When called:** Manually by `MainActivity` when the user clicks "Get Timer Value".

## 4. Lifecycle (Service Only)
- **`onCreate()`:** Starts the background work.
- **`onBind()`:** Establishes the connection with the Activity.
- **`onUnbind()`:** Called when the Activity disconnects.
- **`onDestroy()`:** Stops the background thread and cleans up memory.

## 5. Interface Interaction (UI)
This class has no UI. It provides **data** to the UI (MainActivity).

## 6. Interaction with other components
- **MainActivity:** Connects to the service using a `ServiceConnection`. It calls `getTimerValue()` to update the screen.

## 7. General Logic
The service acts like a background stopwatch. It doesn't care about the UI; it just counts. When the Activity "binds" to it, it's like plugging a monitor into the stopwatch to see the numbers.

**Use Case:**
1. User clicks "Bind Service".
2. `MainActivity` connects to `TimerService`.
3. `TimerService` starts counting (1, 2, 3...).
4. User clicks "Get Timer".
5. `MainActivity` asks the service for the number and displays it.
6. User clicks "Unbind". The service stops and disappears.

## 8. Simplified Explanation
Think of `TimerService` as a **Weather Station** in your backyard.
- The station (Service) is outside, constantly measuring the temperature (timer).
- You are inside the house (Activity).
- When you want to know the temperature, you plug in a display (Bind) to the station.
- If you unplug the display, the station stops working because no one is watching it.

---
**Tip for Students:** Bound services are great for tasks that only need to run while the user is looking at a specific screen. If you need the task to continue after the user leaves, use a Foreground Service or WorkManager instead.
