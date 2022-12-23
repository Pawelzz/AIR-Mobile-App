package com.example.airmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TableLayout
import androidx.core.content.res.ResourcesCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*

class LEDActivity : AppCompatActivity() {

    lateinit var urlText: EditText

    lateinit var redSeekBar: android.widget.SeekBar
    lateinit var greenSeekBar: android.widget.SeekBar
    lateinit var blueSeekBar: android.widget.SeekBar
    lateinit var colorView: View
    lateinit var url: String

    var ledActiveColorA = 0
    var ledActiveColorR = 0
    var ledActiveColorG = 0
    var ledActiveColorB = 0

    var ledActiveColor = 0
    var ledOffColor = 0
    var ledOffColorVec: Vector<Int>? = null

    var ledDisplayModel = Array(8) {
        Array(8) {
            arrayOfNulls<Int>(3)
        }
    }

    private var queue: RequestQueue? = null
    var paramsClear: HashMap<String, String> = HashMap()
    private var response: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led)

        var btnNext1 = findViewById<Button>(R.id.btn_main)
        btnNext1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        var btnNext2 = findViewById<Button>(R.id.btn_plot)
        btnNext2.setOnClickListener {
            val intent = Intent(this, PlotActivity::class.java)
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

        ledOffColor =
            ResourcesCompat.getColor(resources, R.color.ledIndBackground, null)
        ledOffColorVec = intToRgb(ledOffColor)

        ledActiveColor = ledOffColor

        ledActiveColorR = 0x00
        ledActiveColorG = 0x00
        ledActiveColorB = 0x00

        redSeekBar = findViewById<SeekBar>(R.id.seekBarR)
        greenSeekBar = findViewById<SeekBar>(R.id.seekBarG)
        blueSeekBar = findViewById<SeekBar>(R.id.seekBarB)
        colorView = findViewById(R.id.colorView)

        url = "http://192.168.56.15/moj_led.php"

        clearDisplayModel()

        redSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressChangedValue = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                ledActiveColor = seekBarUpdate('R', progressChangedValue)
                colorView.setBackgroundColor(ledActiveColor)
            }

        })

        greenSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressChangedValue = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                ledActiveColor = seekBarUpdate('G', progressChangedValue)
                colorView.setBackgroundColor(ledActiveColor)
            }

        })

        blueSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressChangedValue = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                ledActiveColor = seekBarUpdate('B', progressChangedValue)
                colorView.setBackgroundColor(ledActiveColor)
            }

        })

        colorView = findViewById(R.id.colorView)

        urlText = findViewById(R.id.urlText)
        urlText.setText(url)

        queue = Volley.newRequestQueue(this)

        for (i in 0..7) {
            for (j in 0..7) {
                // "LEDij" : "[i,j,r,g,b]"
                val data = "[" + i.toString() + "," + j.toString() + ",0,0,0]"
                paramsClear.put(ledIndexToTag(i, j), data)
            }
        }
    }

    fun argbToInt(_a: Int, _r: Int, _g: Int, _b: Int): Int {
        return _a and 0xff shl 24 or (_r and 0xff shl 16) or (_g and 0xff shl 8) or (_b and 0xff)
    }


    fun intToRgb(argb: Int): Vector<Int> {
        val _r = argb shr 16 and 0xff
        val _g = argb shr 8 and 0xff
        val _b = argb and 0xff
        val rgb = Vector<Int>(3)
        rgb.add(0, _r)
        rgb.add(1, _g)
        rgb.add(2, _b)
        return rgb
    }

    fun ledTagToIndex(tag: String): Vector<Int> {
        // Tag: 'LEDxy"
        val vec = Vector<Int>(2)
        vec.add(0, Character.getNumericValue(tag[3]))
        vec.add(1, Character.getNumericValue(tag[4]))
        return vec
    }

    fun ledIndexToTag(x: Int, y: Int): String {
        return "LED$x$y"
    }

    fun ledIndexToJsonData(x: Int, y: Int): String {
        val _x = x.toString()
        val _y = y.toString()
        val _r = (ledDisplayModel[x][y][0]!!).toString()
        val _g = (ledDisplayModel[x][y][1]!!).toString()
        val _b = (ledDisplayModel[x][y][2]!!).toString()
        return "[$_x,$_y,$_r,$_g,$_b]"
    }

    fun ledColorNotNull(x: Int, y: Int): Boolean {
        return !(ledDisplayModel[x][y][0] == null || ledDisplayModel[x][y][1] == null || ledDisplayModel[x][y][2] == null)
    }

    fun clearDisplayModel() {
        for (i in 0..7) {
            for (j in 0..7) {
                ledDisplayModel[i][j][0] = 0
                ledDisplayModel[i][j][1] = 0
                ledDisplayModel[i][j][2] = 0
            }
        }
    }

    fun seekBarUpdate(color: Char, value: Int): Int {
        when (color) {
            'R' -> ledActiveColorR = value
            'G' -> ledActiveColorG = value
            'B' -> ledActiveColorB = value
            else -> {}
        }
        ledActiveColorA = (ledActiveColorR + ledActiveColorG + ledActiveColorB) / 3
        return argbToInt(ledActiveColorA, ledActiveColorR, ledActiveColorG, ledActiveColorB)
    }


    fun changeLedIndicatorColor(v: View) {
        // Set active color as background
        v.setBackgroundColor(ledActiveColor)
        // Find element x-y position
        val tag = v.tag as String
        val index: Vector<Int> = ledTagToIndex(tag)
        val x = index[0] as Int
        val y = index[1] as Int
        // Update LED display data model
        ledDisplayModel[x][y][0] = ledActiveColorR
        ledDisplayModel[x][y][1] = ledActiveColorG
        ledDisplayModel[x][y][2] = ledActiveColorB
    }

    fun clearAllLed(v: View) {
        val tb = findViewById<View>(R.id.ledTable) as TableLayout
        var ledInd: View
        for (i in 0..7) {
            for (j in 0..7) {
                ledInd = tb.findViewWithTag(ledIndexToTag(i, j))
                ledInd.setBackgroundColor(ledOffColor)
            }
        }

        clearDisplayModel()

        sendClearRequest()
    }


    fun getDisplayControlParams(): Map<String, String> {
        var led: String
        var position_color_data: String
        val params: MutableMap<String, String> = HashMap()
        for (i in 0..7) {
            for (j in 0..7) {
                if (ledColorNotNull(i, j)) {
                    led = ledIndexToTag(i, j)
                    position_color_data = ledIndexToJsonData(i, j)
                    params.put(led, position_color_data)
                }
            }
        }
        return params
    }

    fun sendControlRequest(v: View) {
        url = urlText.text.toString()
        Log.i("URL", url)
        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  
                  $response
                  """.trimIndent()
                )
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
            override fun getParams(): Map<String, String> {
                return getDisplayControlParams()
            }
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)
    }

    fun sendClearRequest() {
        url = urlText.text.toString()
        val postRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                Log.d("Response", response!!)
                // TODO: check if ACK is valid
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
            override fun getParams(): Map<String, String> {
                return paramsClear
            }
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)
    }


    fun sendGetRequest() {
        url = urlText.text.toString()
        val getRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                Log.d("Response", response!!)
                // TODO: check if ACK is valid
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
//            override fun getParams(): Map<String, String> {
//                return paramsClear
//            }
        }
        getRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(getRequest)
    }

    fun sendGet(v: View) {
        sendGetRequest()
    }

}