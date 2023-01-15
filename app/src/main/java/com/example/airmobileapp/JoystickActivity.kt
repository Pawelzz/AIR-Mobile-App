package com.example.airmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TableLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_joystick.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.util.*
import org.json.JSONObject
import org.json.JSONTokener
import kotlin.reflect.typeOf



class JoystickActivity : AppCompatActivity() {

    lateinit var url: String

    private var queue: RequestQueue? = null
    private var response: String = ""


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


        url = "http://$ip:$port/control.php"

        queue = Volley.newRequestQueue(this)

        updateValues()
    }

    fun onBtnUp(v: View) {
        val specific_url = "$url?task=click&button=up"
        Log.i("URL", specific_url)
        val postRequest: StringRequest = object : StringRequest(
            Method.GET, specific_url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  $response
                  """.trimIndent()
                )
                updateValues()
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                }
            }
        ) {
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)
    }

    fun onBtnDown(v: View) {
        val specific_url = "$url?task=click&button=down"
        Log.i("URL", specific_url)
        val postRequest: StringRequest = object : StringRequest(
            Method.GET, specific_url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  
                  $response
                  """.trimIndent()
                )
                updateValues()
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)

    }

    fun onBtnLeft(v: View) {
        val specific_url = "$url?task=click&button=left"
        Log.i("URL", specific_url)
        val postRequest: StringRequest = object : StringRequest(
            Method.GET, specific_url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  
                  $response
                  """.trimIndent()
                )
                updateValues()
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)

    }

    fun onBtnRight(v: View) {
        val specific_url = "$url?task=click&button=right"
        Log.i("URL", specific_url)
        val postRequest: StringRequest = object : StringRequest(
            Method.GET, specific_url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  
                  $response
                  """.trimIndent()
                )
                updateValues()
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)

    }

    fun onBtnCenter(v: View) {
        val specific_url = "$url?task=click&button=mid"
        Log.i("URL", specific_url)
        val postRequest: StringRequest = object : StringRequest(
            Method.GET, specific_url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  
                  $response
                  """.trimIndent()
                )
                updateValues()
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)

    }


    fun updateValues(){
        val specific_url = "$url?task=joystick"
        Log.i("URL", specific_url)
        val postRequest: StringRequest = object : StringRequest(
            Method.GET, specific_url,
            Response.Listener { response ->
                if (response != "ACK") Log.d(
                    "Response",
                    """
                  
                  $response
                  """.trimIndent()
                )
                Counter_MID.text = JSONObject(response).get("counter_mid").toString()
                Counter_X.text = JSONObject(response).get("counter_x").toString()
                Counter_Y.text = JSONObject(response).get("counter_y").toString()
            },
            Response.ErrorListener { error ->
                val msg = error.message
                if (msg != null) Log.d("Error.Response", msg) else {
                    // TODO: error type specific code
                }
            }
        ) {
        }
        postRequest.retryPolicy = DefaultRetryPolicy(
            5000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(postRequest)

    }
}