package com.example.airmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.*
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
    private val values = arrayOf("Pressure", "Humidity", "Temperature")
    var val1: String = ""
    var val2: String = ""

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
//        Log.i("onCreate", "PLOT ACTIVITY CREATED")

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

        // access the first spinner
        val spinner1 = findViewById<Spinner>(R.id.spinner1)
        if (spinner1 != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, values)
            spinner1.adapter = adapter
            spinner1.setSelection(0)
            val1 = values[0]

            spinner1.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    val1 = values[position]
                    server.val1 = val1
                    chartUpdate()
                    Toast.makeText(this@PlotActivity, "First signal: " + values[position],
                        Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        // access the second spinner
        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        if (spinner2 != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, values)
            spinner2.adapter = adapter
            spinner2.setSelection(1)
            val2 = values[1]

            spinner2.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    val2 = values[position]
                    server.val2 = val2
                    chartUpdate(is_second = true)
                    Toast.makeText(this@PlotActivity, "Second signal: " + values[position],
                        Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Log.i("onDestroy", "PLOT ACTIVITY DESTROYED")
//    }

    override fun onPause() {
        super.onPause()
//        timer.cancel()
//        timer.purge()
//        resetData()
        started = false
        Log.i("onPause", "PLOT ACTIVITY PAUSED")
    }

//    override fun onResume() {
//        super.onResume()
////        prepareTimer()
//        Log.i("onResume", "PLOT ACTIVITY RESUMED")
//    }

    // ***********************************************
//    inner class MinMaxFilter() : InputFilter {
//        private var intMin: Int = 0
//        private var intMax: Int = 0
//
//        // Initialized
//        constructor(minValue: Int, maxValue: Int) : this() {
//            this.intMin = minValue
//            this.intMax = maxValue
//        }
//
//        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
//            try {
//                val input = Integer.parseInt(dest.toString() + source.toString())
//                if (isInRange(intMin, intMax, input)) {
//                    return null
//                }
//            } catch (e: NumberFormatException) {
//                e.printStackTrace()
//            }
//            return ""
//        }
//
//        // Check if input c is in between min a and max b and
//        // returns corresponding boolean
//        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
//            return if (b > a) c in a..b else c in b..a
//        }
//    }

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
        signal1 = LineGraphSeries()
        signal2 = LineGraphSeries()

        chart.addSeries(signal1)
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

    private fun chartUpdate(is_second: Boolean = false) {
        Log.i("UPDATE", "I'm in chartUpdate()")
        if (is_second) {
            signal2.resetData(arrayOf<DataPoint>())
        }
        else {
            signal1.resetData(arrayOf<DataPoint>())
        }
        var unit1 = ""
        var unit2 = ""
        signal1.title = val1
        signal2.title = val2
        when(val1){
            "Pressure" -> {
                chart.viewport.setMinY(260.0)
                chart.viewport.setMaxY(1260.0)
                unit1 = "hPa"
            }
            "Humidity" -> {
                chart.viewport.setMinY(0.0)
                chart.viewport.setMaxY(100.0)
                unit1 = "%"
                Log.i("VAL1", "humidity")
            }
            "Temperature" -> {
                chart.viewport.setMinY(-30.0)
                chart.viewport.setMaxY(105.0)
                unit1 = "\u00B0" + "C"
            }
            else -> {
                println("Value is unknown")
            }
        }

        when(val2){
            "Pressure" -> {
                chart.secondScale.setMinY(260.0)
                chart.secondScale.setMaxY(1260.0)
                unit2 = "hPa"
            }
            "Humidity" -> {
                chart.secondScale.setMinY(0.0)
                chart.secondScale.setMaxY(100.0)
                unit2 = "%"
            }
            "Temperature" -> {
                chart.secondScale.setMinY(-30.0)
                chart.secondScale.setMaxY(105.0)
                unit2 = "\u00B0" + "C"
                Log.i("VAL2", "temperature")
            }
            else -> {
                println("Value is unknown")
            }
        }
        chart.gridLabelRenderer.verticalAxisTitle = "$val1/$val2 [$unit1/$unit2]"
//        chart.gridLabelRenderer.invalidate(true, true)
        chart.onDataChanged(true, false)
    }
}