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

class ServerTable(private val ip: String, context: Context) {
    private var protocol: String = "http://"
    private var script: String = "/control.php?task=table"
    private var timeout: Int = 1000
    var requestCounter: Int = -1
    private var response: String = ""
    private lateinit var json: JSONObject
    private var queue: RequestQueue = Volley.newRequestQueue(context.applicationContext)

    fun getMeasurements(t: Double): Array<Double> {
        Log.i("CHECK IP TABLE", ip)
        val time = timeout/t
        val result = getResponse(time)
        json = JSONTokener(result).nextValue() as JSONObject
        val roll = json.getDouble("Roll")
        val pitch = json.getDouble("Pitch")
        val yaw = json.getDouble("Yaw")
        val press = json.getDouble("Pressure")
        val temp = json.getDouble("Temperature")
        val humid = json.getDouble("Humidity")
        val cMid = json.getDouble("counter_mid")
        val cX = json.getDouble("counter_x")
        val cY = json.getDouble("counter_y")
        return arrayOf(roll, pitch, yaw, press, temp, humid, cMid, cX, cY)
    }

    fun resetRequestCounter() {
        requestCounter = -1
    }

    private fun getResponse(t: Double): String {
        val url = "$protocol$ip:$port$script"
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
            response = future.get(time*2, TimeUnit.MILLISECONDS)
            Log.i("##RESPONSE##", response)
            response
        }catch (e: InterruptedException) {
            e.printStackTrace()
            return response
        } catch (e: TimeoutException) {
            Log.i("##TIMEOUT##", response)
            e.printStackTrace()
            return response
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return response
        }
    }
}