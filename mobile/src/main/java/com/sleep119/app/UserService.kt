package com.sleep119.app

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object UserService {
    private var baseRoute: String = "/user"
    private var url: String = "http://3.34.97.168:8000$baseRoute"

    fun addUser(context: Context, jsonObject: JSONObject, success: (JSONObject) -> Unit) {
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
                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(context).add(request)
    }

    fun getUser(context: Context, userId: Int, success: (JSONObject) -> Unit) {
        val request = StringRequest(
            Request.Method.GET, "$url/$userId",
            { res ->
                val parsedRes = parseJSON(res)
                success(parsedRes)
            },
            { error ->
                JSONObject(error.stackTraceToString())
            }
        )
        request.setShouldCache(false)
        Volley.newRequestQueue(context).add(request)
    }

    fun updateUser(context: Context, userId: Int, jsonObject: JSONObject, success: (JSONObject) -> Unit) {
        val request = object: StringRequest(
            Method.PATCH, "$url/$userId",
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
                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(context).add(request)
    }

    fun deleteUser(context: Context, userId: Int, success: (JSONObject) -> Unit) {
        val request = StringRequest(
            Request.Method.DELETE, "$url/$userId",
            { res ->
                val parsedRes = parseJSON(res)
                success(parsedRes)
            },
            { error ->
                JSONObject(error.stackTraceToString())
            }
        )
        request.setShouldCache(false)
        Volley.newRequestQueue(context).add(request)
    }

    private fun parseJSON(response: String): JSONObject {
        val utf8String = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        return JSONObject(utf8String)
    }
}