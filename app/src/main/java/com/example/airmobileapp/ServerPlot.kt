package com.example.airmobileapp

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONTokener
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ServerPlot(private val ip: String, context: Context) {
    private var protocol: String = "http://"
    var val1: String = "pressure=Pa"
    var val2: String = "humidity=Prcnt"
    private var script: String = "/control.php?task=sensors&$val1&$val2"
    private var timeout: Int = 1000
    var requestCounter: Int = -1
    private var signal1val: Double = 0.0
    private var signal2val: Double = 0.0
    private var response: String = ""
    private lateinit var json: JSONObject
    private var queue: RequestQueue = Volley.newRequestQueue(context.applicationContext)

    fun getSignals(t: Double): Array<Double> {
        Log.i("CHECK IP PLOT", ip)
        val time = timeout/t
        updateScript()
        val result = getResponse(time)
        json = JSONTokener(result).nextValue() as JSONObject
//        signal1val = json.getDouble("Pressure")
//        signal2val = json.getDouble("Humidity")
//        Log.i("Pressure", signal1val.toString())
//        Log.i("Humidity", signal2val.toString())
        signal1val = json.getDouble(val1)
        signal2val = json.getDouble(val2)
        Log.i(val1, signal1val.toString())
        Log.i(val2, signal2val.toString())
        return arrayOf(signal1val, signal2val)
    }

    fun resetRequestCounter() {
        requestCounter = -1
    }

    private fun getResponse(t: Double): String {
        val url: String = protocol + ip + script
//        val url: String = "https://api.coingecko.com/api/v3/ping"
        Log.i("##URL##", url)
        val time = t.toLong()

        // Create future request
        val future = RequestFuture.newFuture<String>()
        val stringRequest = StringRequest(Request.Method.GET, url, future, future)


        // Add the request to the RequestQueue.
        queue.add(stringRequest)
        requestCounter++

        return try{
            response = future.get(time, TimeUnit.MILLISECONDS)
            Log.i("##RESPONSE##", response)
            response
        }catch (e: InterruptedException) {
            e.printStackTrace()
            return response
        } catch (e: TimeoutException) {
            e.printStackTrace()
            return response
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return response
        }
    }

    private fun updateScript() {
        var sig1 = ""
        var sig2 = ""
        when (val1) {
            "Pressure" -> {
                sig1 = "pressure=Pa"
            }
            "Humidity" -> {
                sig1 = "humidity=Prcnt"
            }
            "Temperature" -> {
                sig1 = "temperature=C"
            }
            else -> {
                println("Value is unknown")
            }
        }

        when (val2) {
            "Pressure" -> {
                sig2 = "pressure=Pa"
            }
            "Humidity" -> {
                sig2 = "humidity=Prcnt"
            }
            "Temperature" -> {
                sig2 = "temperature=C"
            }
            else -> {
                println("Value is unknown")
            }
        }
        script = "/control.php?task=sensors&$sig1&$sig2"
    }
}