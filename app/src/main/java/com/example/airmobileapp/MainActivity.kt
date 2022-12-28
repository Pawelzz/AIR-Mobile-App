package com.example.airmobileapp

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

var ip: String = "192.168.56.15"
var port: Int = 80
var sampling: Double = 1.0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnNext1 = findViewById<Button>(R.id.btn_LED)
        btnNext1.setOnClickListener {
            val intent = Intent(this, LEDActivity::class.java)
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
        btn_save.setOnClickListener {
            ip = ip_input.text.toString()
            port = port_input.text.toString().toInt()
            sampling = sampling_input.text.toString().toDouble()
            Log.i("onClick", "MAIN ACTIVITY ON CLICK")
        }
        Log.i("onCreate", "MAIN ACTIVITY CREATED")
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view != null && view is EditText) {
                val r = Rect()
                view.getGlobalVisibleRect(r)
                val rawX = ev.rawX.toInt()
                val rawY = ev.rawY.toInt()
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus()
                    hideKeyboard(view)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "MAIN ACTIVITY DESTROYED")
    }

    override fun onPause() {
        super.onPause()
        Log.i("onPause", "MAIN ACTIVITY PAUSED")
    }

    override fun onResume() {
        super.onResume()
        ip_input.setText(ip)
        port_input.setText(port.toString())
        sampling_input.setText(sampling.toString())

        Log.i("onResume", "MAIN ACTIVITY RESUMED")
    }
}