package ro.pub.cs.systems.eim.colocviu1_1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Colocviu1_1MainActivity : AppCompatActivity() {

    private lateinit var northButton: Button
    private lateinit var eastButton: Button
    private lateinit var westButton: Button
    private lateinit var southButton: Button
    private lateinit var textView: TextView
    private lateinit var changeActivityButton: Button

    var nrPoints = 0

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
        } else if (view.id == R.id.changeActivityButton) {
            // TODO
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
}