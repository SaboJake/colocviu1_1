package ro.pub.cs.systems.eim.colocviu1_1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Coloclviu1_1SecondaryActivity : AppCompatActivity() {
    private lateinit var currentInstructionsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coloclviu1_1secondary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val message = intent.getStringExtra(Constants.MESSAGE)
        currentInstructionsText = findViewById(R.id.currentInstructionsText)
        currentInstructionsText.text = message

        setResult(Activity.RESULT_CANCELED) // System Back defaults to cancel too.

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            setResult(Activity.RESULT_OK,
                Intent().putExtra(Constants.MESSAGE, "Directions: $message"))
            finish()
        }
        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            setResult(Activity.RESULT_CANCELED,
                Intent().putExtra(Constants.MESSAGE, "Canceled"))
            finish()
        }
    }




}