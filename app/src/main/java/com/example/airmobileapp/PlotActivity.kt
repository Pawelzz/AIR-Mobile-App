package com.example.airmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.graphics.Color
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_plot.*
import java.util.*

class PlotActivity : AppCompatActivity() {
    lateinit var chart: GraphView
    lateinit var signal1: LineGraphSeries<DataPoint>
    lateinit var signal2: LineGraphSeries<DataPoint>
    lateinit var server: ServerPlot
    private var sampleFreq: Double = 0.0
    private var sampleMax: Int = 0
    private var counter: Int = 0
    private lateinit var timer: Timer
    private var started: Boolean = false

    // ***********************************************

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot)

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
        var btnNext3 = findViewById<Button>(R.id.btn_joystick)
        btnNext3.setOnClickListener {
            val intent = Intent(this, JoystickActivity::class.java)
            startActivity(intent)
        }
        var btnNext4 = findViewById<Button>(R.id.btn_table)
        btnNext4.setOnClickListener {
            val intent = Intent(this, TableActivity::class.java)
            startActivity(intent)
        }

        chartInit()
        server = ServerPlot("192.168.56.15", this)

        // Bind OnClickListener to button
        runBtn.setOnClickListener {
            if(!started) {
                sampleFreq = freqField.text.toString().toDouble()
                sampleMax = (chart.viewport.getMaxX(false) * sampleFreq).toInt()
                Log.i("SAMPLE FREQ", sampleFreq.toString())

                server.resetRequestCounter()
                resetData()
                counter = 0

                timer = Timer()
                val timerTask: TimerTask = object : TimerTask() {
                    override fun run() {
                        getData()
                    }
                }
                val period = 1.0/sampleFreq*1000
                Log.i("Period", period.toString())
                timer.scheduleAtFixedRate(timerTask, 0, period.toLong())
            }
            started = true
        }
    }

    // ***********************************************
    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    // ***********************************************
    private fun resetData() {
        signal1.resetData(arrayOf<DataPoint>())
        signal2.resetData(arrayOf<DataPoint>())
    }

    // ***********************************************
    private fun getData() {
        if(counter <= sampleMax) {
//            val signals: Array<Double>
            val signals: Array<Double>  = server.getSignals(sampleFreq)
            val t = counter/sampleFreq

            // display data (GraphView)
            runOnUiThread {
                signal1.appendData(DataPoint(t, signals[0]), false, sampleMax)
                signal2.appendData(DataPoint(t, signals[1]), false, sampleMax)
                chart.onDataChanged(true, true)
            }
            counter++
        } else {
            timer.cancel()
            started = false
        }

    }

    // ***********************************************
    private fun chartInit() {
        chart = findViewById(R.id.chart)
//        val signal1: LineGraphSeries<DataPoint> = LineGraphSeries()
//        val signal2: LineGraphSeries<DataPoint> = LineGraphSeries()
        signal1 = LineGraphSeries()
        signal2 = LineGraphSeries()

        chart.addSeries(signal1)
//        chart.addSeries(signal2)
        chart.secondScale.addSeries(signal2)

        chart.viewport.isXAxisBoundsManual = true
        chart.viewport.setMinX(0.0)
        chart.viewport.setMaxX(20.0)
        chart.viewport.isScrollable = true
        chart.viewport.isScalable = true
        chart.viewport.isYAxisBoundsManual = true
        chart.viewport.setMinY(260.0)
        chart.viewport.setMaxY(1260.0)
        chart.secondScale.setMinY(0.0)
        chart.secondScale.setMaxY(100.0)

        signal1.title = "Pressure"
        signal1.color = Color.BLUE
        signal2.title = "Humidity"
        signal2.color = Color.RED

        chart.legendRenderer.isVisible = true
        chart.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        chart.legendRenderer.textSize = 30f

        chart.gridLabelRenderer.textSize = 30f
        chart.gridLabelRenderer.verticalAxisTitle = "Pressure/Humidity [hPa/%]"
        chart.secondScale.verticalAxisTitle = "Humidity [%]"
        chart.gridLabelRenderer.horizontalAxisTitle = "Time [s]"
        chart.gridLabelRenderer.numHorizontalLabels = 10
        chart.gridLabelRenderer.numVerticalLabels = 10
        chart.gridLabelRenderer.padding = 25
        chart.gridLabelRenderer.labelsSpace = 5


    }
}