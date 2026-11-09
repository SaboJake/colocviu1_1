package ro.pub.cs.systems.eim.colocviu1_1

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Colocviu1_1MainActivity : AppCompatActivity() {

    private lateinit var northButton: Button
    private lateinit var eastButton: Button
    private lateinit var westButton: Button
    private lateinit var southButton: Button
    private lateinit var textView: TextView
    private lateinit var changeActivityButton: Button


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action != Constants.ACTION_STRING) return
            val time = intent.getStringExtra(Constants.DATE).orEmpty()
            val msg = intent.getStringExtra(Constants.MESSAGE).orEmpty()
            val message = "$time | msg = $msg"
            Log.d(Constants.TAG, "| TYPE: ${intent.action} | RECEIVED: $message")
            // Temporary visual confirmation while debugging.
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    private var receiverRegistered = false
    var nrPoints = 0


    private val secondaryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val label = if (result.resultCode == Activity.RESULT_OK) "OK" else "Cancel"
        val message = result.data?.getStringExtra(Constants.MESSAGE).orEmpty()
        Toast.makeText(this, "$label ($message)", Toast.LENGTH_SHORT).show()
    }

    private fun ensureServiceStarted() {
        if (nrPoints > Constants.THRESHOLD) {
            startService(Intent(this, Coloclviu1_1Service::class.java).apply {
                putExtra(Constants.MESSAGE, textView.text.toString())
            })
        }
    }
    private val buttonClickListener = View.OnClickListener { view ->
        if (view.id in setOf(R.id.northButton, R.id.eastButton, R.id.westButton, R.id.southButton)) {
            val buttonText = (view as Button).text.toString()
            textView.text = buildString {
                append(textView.text.toString())
                append(" ")
                append(buttonText)
            }
            nrPoints++
            Log.d(Constants.TAG, "nrPoints: $nrPoints")
            ensureServiceStarted()
        } else if (view.id == R.id.changeActivityButton) {
            // TODO
            val intent = Intent(this, Coloclviu1_1SecondaryActivity::class.java)
            intent.putExtra(Constants.MESSAGE, textView.text.toString())
            secondaryLauncher.launch(intent)

            nrPoints = 0
            textView.text = ""
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_colocviu1_1_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.southButton)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nrPoints = savedInstanceState?.getInt(Constants.NRPOINTS) ?: 0

        northButton = findViewById(R.id.northButton)
        eastButton = findViewById(R.id.eastButton)
        westButton = findViewById(R.id.westButton)
        southButton = findViewById(R.id.southButton)

        northButton.setOnClickListener(buttonClickListener)
        eastButton.setOnClickListener(buttonClickListener)
        westButton.setOnClickListener(buttonClickListener)
        southButton.setOnClickListener(buttonClickListener)


        textView = findViewById(R.id.textView)
        changeActivityButton = findViewById(R.id.changeActivityButton)
        changeActivityButton.setOnClickListener(buttonClickListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.NRPOINTS, nrPoints)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Constants.ACTION_STRING)
        }
        ContextCompat.registerReceiver(
            this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
        receiverRegistered = true
        // Also restarts after rotation/process recreation if counts exceed threshold.
        // The service ignores repeat starts while its worker is alive.
        ensureServiceStarted()
    }

    override fun onPause() {
        if (receiverRegistered) {
            unregisterReceiver(receiver)
            receiverRegistered = false
        }
        super.onPause()
    }

    override fun onDestroy() {
        stopService(Intent(this, Coloclviu1_1Service::class.java))
        super.onDestroy()
    }
}