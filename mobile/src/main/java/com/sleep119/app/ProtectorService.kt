package com.sleep119.app

import android.content.Context
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

object  ProtectorService {
    private var baseRoute: String = "/protector"
    private var url: String = "http://3.34.97.168:8000$baseRoute"

    fun addProtector(fragment: Fragment, telno: String, name: String, relationship: String, userId: Int, success: (JSONObject) -> Unit) {
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
                jsonObject.put("telno", telno)
                jsonObject.put("name", name)
                jsonObject.put("relationship", relationship)
                jsonObject.put("user_id", userId)

                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun getProtector(fragment: Fragment, protectorId: Int, success: (JSONObject) -> Unit) {
        val request = StringRequest(
            Request.Method.GET, "$url/$protectorId",
            { res ->
                val parsedRes = parseJSON(res)
                success(parsedRes)
            },
            { error ->
                JSONObject(error.stackTraceToString())
            }
        )
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun getProtectorOfUser(context: Context, userId: Int, success: (JSONArray) -> Unit) {
        val request = StringRequest(
            Request.Method.GET, "$url?user_id=$userId",
            { res ->
                val parsedRes = parseJSONArray(res)
                success(parsedRes)
            },
            { error ->
                JSONObject(error.stackTraceToString())
            }
        )
        request.setShouldCache(false)
        Volley.newRequestQueue(context).add(request)
    }

    fun updateProtector(fragment: Fragment, protectorId: Int, telno: String, success: (JSONObject) -> Unit) {
        val request = object: StringRequest(
            Method.PATCH, "$url/$protectorId",
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
                jsonObject.put("telno", telno)

                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun deleteDelete(fragment: Fragment, protectorId: Int, success: (JSONObject) -> Unit) {
        val request = StringRequest(
            Request.Method.DELETE, "$url/$protectorId",
            { res ->
                val parsedRes = parseJSON(res)
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