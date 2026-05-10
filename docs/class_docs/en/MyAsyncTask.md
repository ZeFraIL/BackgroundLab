# MyAsyncTask Class Documentation

## 1. General Information
- **Class Name:** `MyAsyncTask`
- **Type:** Normal Class (extends `AsyncTask`)
- **Purpose:** This class demonstrates the legacy (deprecated) way of performing background tasks in Android. It is used to run a short task (counting to 100) on a background thread and update a `ProgressBar` on the main thread.
- **Interaction:** It is created and executed by `MainActivity`. It holds "WeakReferences" to the Activity and ProgressBar to prevent memory leaks.

## 2. Variables (Class Fields)
| Name | Type | Purpose | Where is it used |
| :--- | :--- | :--- | :--- |
| `activityReference` | `WeakReference<MainActivity>` | A "weak" link to the Activity. | Used to call `log()` and check if the Activity is still alive. |
| `progressBarReference` | `WeakReference<ProgressBar>` | A "weak" link to the progress bar. | Used to update the progress bar on the screen. |

> **What is a WeakReference?** Imagine a normal variable is like a strong rope holding an object. A WeakReference is like a thin thread. If the system needs to clean up the object (like when you rotate the screen), it can break the thin thread easily, preventing "Memory Leaks."

## 3. Class Methods

### Method Name: `onPreExecute`
- **Type:** `protected`
- **Return value:** `void`
- **What it does:** 
    1. Runs on the **Main Thread**.
    2. Makes the `ProgressBar` visible.
    3. Logs that the task is starting.
- **When called:** Automatically right before the background work begins.

### Method Name: `doInBackground`
- **Type:** `protected`
- **Return value:** `String` (Result message)
- **Parameters:** `Void... voids` (Empty parameters)
- **What it does:** 
    1. Runs on a **Background Thread**.
    2. Loops 100 times.
    3. In each loop, it sleeps for 50ms (simulating work) and calls `publishProgress()`.
- **When called:** Automatically after `onPreExecute`.

### Method Name: `onProgressUpdate`
- **Type:** `protected`
- **Return value:** `void`
- **Parameters:** `Integer... values` (The current progress number)
- **What it does:** 
    1. Runs on the **Main Thread**.
    2. Updates the `ProgressBar` with the new value.
- **When called:** Whenever `publishProgress()` is called inside `doInBackground`.

### Method Name: `onPostExecute`
- **Type:** `protected`
- **Return value:** `void`
- **Parameters:** `String result` (The result from `doInBackground`)
- **What it does:** 
    1. Runs on the **Main Thread**.
    2. Hides the `ProgressBar` and logs that the task is finished.
- **When called:** Automatically when `doInBackground` is done.

## 4. Lifecycle
`AsyncTask` does not have a lifecycle like an Activity. However, it is **dangerous** because it can live longer than the Activity that created it. If the Activity is destroyed (e.g., screen rotation) while the task is running, the task might try to update a non-existent UI, causing a crash or leak.

## 5. Interface Interaction (UI)
- **ProgressBar:** Updated via `progressBarReference`.
- **MainActivity:** Logged via `activityReference`.

## 6. Interaction with other components
- **MainActivity:** The Activity starts the task using `new MyAsyncTask(this, progressBar).execute()`.

## 7. General Logic
The class follows a "Before -> During -> After" pattern.
1. **Before:** Show progress bar.
2. **During:** Do math/work in the dark (background).
3. **After:** Show results and hide progress bar.

## 8. Simplified Explanation
Think of `MyAsyncTask` as a **Messenger**.
- You (Activity) tell the messenger to go to the store (Background).
- While at the store, the messenger occasionally shouts back "I'm 50% done!" (Progress Update).
- When the messenger returns with the groceries, he gives them to you (Post Execute).

---
**Tip for Students:** `AsyncTask` is **Deprecated**, meaning Google recommends NOT using it in new projects. Why? Because it's hard to manage when the screen rotates. Use **WorkManager** or **Kotlin Coroutines** instead!
