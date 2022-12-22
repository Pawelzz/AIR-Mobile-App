package com.example.airmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class JoystickActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

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
        var btnNext4 = findViewById<Button>(R.id.btn_table)
        btnNext4.setOnClickListener {
            val intent = Intent(this, TableActivity::class.java)
            startActivity(intent)
        }
    }
}