package com.example.airmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.activity_table.*
import java.util.*

class TableActivity : AppCompatActivity() {
    lateinit var server: ServerTable
    private var sampleFreq: Double = sampling
    private lateinit var timerTable: Timer
    private var run: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        var btnNext1 = findViewById<Button>(R.id.btn_main)
        btnNext1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        var btnNext2 = findViewById<Button>(R.id.btn_LED)
        btnNext2.setOnClickListener {
            val intent = Intent(this, LEDActivity::class.java)
            startActivity(intent)
        }
        var btnNext3 = findViewById<Button>(R.id.btn_plot)
        btnNext3.setOnClickListener {
            val intent = Intent(this, PlotActivity::class.java)
            startActivity(intent)
        }
        var btnNext4 = findViewById<Button>(R.id.btn_joystick)
        btnNext4.setOnClickListener {
            val intent = Intent(this, JoystickActivity::class.java)
            startActivity(intent)
        }
        Log.i("onCreate", "ACTIVITY CREATED")

        server = ServerTable(ip, this)
        server.resetRequestCounter()
        run = true
        timerTable = Timer()
//        prepareTimer()
//        run = true
//        val timerTask: TimerTask = object : TimerTask() {
//            override fun run() {
//                runRequests()
//            }
//        }
//        val period = 1.0/sampleFreq*1000
//        Log.i("Period", period.toString())
//        timer.scheduleAtFixedRate(timerTask, 0, period.toLong())
    }

    private fun runRequests() {
        if(run) {
            val measurements: Array<Double> = server.getMeasurements(sampleFreq)

            runOnUiThread {
                valRoll.text = measurements[0].toString()
                valPitch.text = measurements[1].toString()
                valYaw.text = measurements[2].toString()
                valPress.text = measurements[3].toString()
                valTemp.text = measurements[4].toString()
                valHumidity.text = measurements[5].toString()
                valMid.text = measurements[6].toInt().toString()
                valX.text = measurements[7].toInt().toString()
                valY.text = measurements[8].toInt().toString()
            }
        }
    }

    private fun prepareTimer() {
//        timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                runRequests()
            }
        }
        val period = 1.0/sampleFreq*1000
        Log.i("Period", period.toString())
        timerTable.scheduleAtFixedRate(timerTask, 0, period.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "ACTIVITY DESTROYED")
    }

    override fun onPause() {
        super.onPause()
        run = false
        timerTable.cancel()
        timerTable.purge()
        Log.i("onPause", "ACTIVITY PAUSED")
    }

    override fun onResume() {
        super.onResume()
        prepareTimer()
        Log.i("onResume", "ACTIVITY RESUMED")
    }
}