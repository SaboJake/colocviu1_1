package ro.pub.cs.systems.eim.colocviu1_1

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

class ProcessingThread(
    private val context: Context,
    receivedMessage: String
) : Thread("ExamWorker") {

    @Volatile
    private var running = true

//    fun updateNumbers(left: Int, right: Int) {
//        numbers = Pair(left, right)
//    }

    private var msg = receivedMessage

    override fun run() {
        val formatter = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.US
        )

        try {
            while (running && !isInterrupted) {
                sleep(Constants.PERIOD_MS)
                if (!running || isInterrupted) break
                // Read the latest pair ONCE for this broadcast.
                // Recalculate INSIDE the loop.
                val message = Intent(Constants.ACTION_STRING).apply {
                    setPackage(context.packageName)

                    putExtra(Constants.DATE, formatter.format(Date()))
                    putExtra(Constants.MESSAGE, msg)
                }
                context.sendBroadcast(message)
                Log.d(Constants.TAG, "SENT: date = ${formatter.format(Date())}, messgae: $msg")
                running = false
            }
        } catch (_: InterruptedException) {
            // Expected when cancel() interrupts sleep.
        } finally {
            Log.d(Constants.TAG, "Worker stopped")
        }
    }
    fun cancel() {
        running = false
        interrupt() // Wake Thread.sleep immediately.
    }
}

class Coloclviu1_1Service : Service() {
    private var worker: ProcessingThread? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent == null) {
            if (worker == null) stopSelf(startId)
            return START_NOT_STICKY
        }

//        val left = intent.getIntExtra(Constants.LEFT, 0)
//        val right = intent.getIntExtra(Constants.RIGHT, 0)
        val msg = intent.getStringExtra(Constants.MESSAGE)
        val currentWorker = worker

        if (currentWorker?.isAlive == true) {
//            currentWorker.updateNumbers(left, right)
//            Log.d(Constants.TAG, "UPDATED: left=$left, right=$right")
        } else {
            worker = ProcessingThread(
                applicationContext,
                msg ?: ""
            ).also { it.start() }
            Log.d(Constants.TAG, "STARTED: msg = $msg")
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        worker?.cancel()
        worker = null
        super.onDestroy()
    }
}
