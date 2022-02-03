package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val CHANNEL_ID = "org.hyperskill"

class MainActivity : AppCompatActivity() {
    private val handler = Handler()
    private var isStart = true
    private var seconds = 0
    private var minutes = 0
    private var progressBarColor = Color.BLUE
    private var upperLimit = Int.MAX_VALUE
    private lateinit var textColorDefault: ColorStateList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar.visibility = View.INVISIBLE
        resetButton.isEnabled = false
        textColorDefault = textView.textColors
        /**
         * Start button
         */
        startButton.setOnClickListener {
            if (isStart) {
                handler.postDelayed(startTime, 1000)
                isStart = false
                progressBar.visibility = View.VISIBLE
                settingsButton.isEnabled = false
                startButton.isEnabled = false
                resetButton.isEnabled = true
            }
        }
        /**
         * Reset button
         */
        resetButton.setOnClickListener {
            handler.removeCallbacks(startTime)
            textView.text = getString(R.string.start_time)
            textView.setTextColor(textColorDefault)
            seconds = 0
            minutes = 0
            isStart = true
            progressBar.visibility = View.INVISIBLE
            settingsButton.isEnabled = true
            resetButton.isEnabled = false
            startButton.isEnabled = true
        }
        /**
         * Settings button
         */
        settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.prompt, null, false)
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.settings)
            builder.setMessage("Set upper limit in seconds")
            builder.setView(contentView)
            val input = contentView.findViewById<EditText>(R.id.upperLimitEditText)
            if (upperLimit != Int.MAX_VALUE) {
                input.setText(upperLimit.toString())
            }
            builder.setPositiveButton(android.R.string.ok) { _, _ ->

                if (input.text.isNotEmpty()) {
                    upperLimit = input.text.toString().toInt()
                }
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.show()
        }
    }

    /**
     * Create Runnable object
     */
    private val startTime: Runnable = object : Runnable {
        override fun run() {

            seconds += 1
            if (seconds == 60) {
                seconds = 0
                minutes += 1
            }
            val time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            progressBar.indeterminateTintList = ColorStateList.valueOf(getColor())
            textView.text = time
            if (seconds >= upperLimit) {
                textView.setTextColor(Color.RED)
                createNotification(baseContext)
            }
            handler.postDelayed(this, 1000)
        }
    }

    /**
     * Create notification that the time has expired
     *
     * @param context
     */
    fun createNotification(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Time status"
            val descriptionText = "Time exceeded"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_time)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(393939, notificationBuilder.build())
    }

    /**
     * Get random color
     *
     * @return color
     */
    fun getColor(): Int {
        val colors = arrayOf(
            Color.BLACK,
            Color.BLUE,
            Color.CYAN,
            Color.DKGRAY,
            Color.GRAY,
            Color.GREEN,
            Color.LTGRAY,
            Color.RED,
            Color.YELLOW,
            Color.MAGENTA
        )
        var newColor = colors[(colors.indices).random()]
        while (newColor == progressBarColor) {
            newColor = colors[(colors.indices).random()]
        }
        progressBarColor = newColor
        return newColor
    }
}

