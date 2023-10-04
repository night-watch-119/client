package com.sleep119.app

import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

object HealthInfoService {
    private var baseRoute: String = "/healthInfo"
    private var url: String = "http://3.34.97.168:8000$baseRoute"

    fun addHealthInfo(fragment: Fragment, heartRate: Int, oxygenSaturation: String, userId: Int, success: (JSONObject) -> Unit) {
        val request = object: StringRequest(
            Method.POST, "$url/",
            { res ->
                val parsedRes = parseJSON(res)
                success(parsedRes)
            },
            { error ->
                JSONObject(error.stackTraceToString())
            }
        ){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                val jsonObject = JSONObject()
                jsonObject.put("heart_rate", heartRate)
                jsonObject.put("oxygen_saturation", oxygenSaturation)
                jsonObject.put("user_id", userId)

                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun getHealthInfoForDuration(fragment: Fragment, userId: Int, year: Int, month: Int, day: Int, duration: String, success: (JSONArray) -> Unit) {
        val request = StringRequest(
            Request.Method.GET, "$url?user_id=$userId&year=$year&month=$month&day=$day&duration=$duration",
            { res ->
                val parsedRes = parseJSONArray(res)
                success(parsedRes)
            },
            { error ->
                JSONObject(error.stackTraceToString())
            }
        )
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    private fun parseJSON(response: String): JSONObject {
        val utf8String = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        return JSONObject(utf8String)
    }

    private fun parseJSONArray(response: String): JSONArray {
        val utf8String = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        return JSONArray(utf8String)
    }
}