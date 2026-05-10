# Chained Workers Documentation

This document describes the group of classes used to demonstrate **Work Chaining** with WorkManager. Work chaining allows you to run multiple tasks in a specific order (Task A -> Task B -> Task C).

## 1. General Information
All classes below extend the `Worker` class. They are responsible for a single step in a multi-step process.

- **DownloadWorker:** Simulates fetching a file from the internet.
- **UnpackWorker:** Simulates extracting contents from a zip file.
- **SaveToDbWorker:** Simulates inserting the extracted data into a database.
- **UploadWorker:** Simulates sending a report back to a server (used with constraints).
- **PhotoCompressionWorker:** A standalone task that simulates reducing image file size.

## 2. Variables (Class Fields)
All these classes primarily use a `TAG` string for logging progress to the Android Logcat.

## 3. Class Methods (doWork)
Each class implements the `doWork()` method similarly:
1. Logs that the specific step has started.
2. Uses `Thread.sleep()` (usually 1-2 seconds) to simulate time-consuming work.
3. Returns `Result.success()` to allow the next worker in the chain to start.

## 4. Interaction & Logic
In `MainActivity`, these workers are linked like this:
```java
WorkManager.getInstance()
    .beginWith(downloadWork)
    .then(unpackWork)
    .then(saveWork)
    .enqueue();
```
- **If DownloadWorker fails:** The system will NOT start `UnpackWorker`.
- **If DownloadWorker succeeds:** The system automatically triggers `UnpackWorker`.

## 5. Specific Roles

### 📌 Class: `DownloadWorker`
- **Role:** The "Starter."
- **Logic:** Waits for network connection (if constraints are set) and "downloads" data.

### 📌 Class: `UnpackWorker`
- **Role:** The "Processor."
- **Logic:** Takes the "downloaded" file and processes it.

### 📌 Class: `SaveToDbWorker`
- **Role:** The "Finalizer."
- **Logic:** Takes the processed data and saves it permanently.

### 📌 Class: `UploadWorker`
- **Special Feature:** Usually used with **Constraints**. For example, it only runs if the phone is charging and connected to Wi-Fi.

### 📌 Class: `PhotoCompressionWorker`
- **Role:** Standalone background utility.
- **Logic:** Reduces the weight of a file to save space.

## 6. Simplified Explanation
Think of these workers as a **Relay Race Team**.
- **DownloadWorker** is the first runner. He runs his lap and passes the baton to **UnpackWorker**.
- **UnpackWorker** cannot start running until he gets the baton.
- If one runner trips and falls (fails), the race stops, and the finish line (Database) is never reached.

---
**Tip for Students:** Using a chain is much better than putting all the code in one big class. It makes the code easier to read, test, and debug!
