package com.sleep119.app

import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object UserService {
    private var baseRoute: String = "/user"
    private var url: String = "http://3.34.97.168:8000$baseRoute"

    fun addUser(fragment: Fragment, name: String, bloodType: String, telno: String, success: (JSONObject) -> Unit) {
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
                jsonObject.put("name", name)
                jsonObject.put("blood_type", bloodType)
                jsonObject.put("telno", telno)

                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun getUser(fragment: Fragment, userId: Int, success: (JSONObject) -> Unit) {
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
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun updateUser(fragment: Fragment, userId: Int, bloodType: String, telno: String, success: (JSONObject) -> Unit) {
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
                val jsonObject = JSONObject()
                jsonObject.put("blood_type", bloodType)
                jsonObject.put("telno", telno)

                val requestBody = jsonObject.toString()
                return requestBody.toByteArray()
            }
        }
        request.setShouldCache(false)
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    fun deleteUser(fragment: Fragment, userId: Int, success: (JSONObject) -> Unit) {
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
        Volley.newRequestQueue(fragment.requireContext()).add(request)
    }

    private fun parseJSON(response: String): JSONObject {
        val utf8String = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        return JSONObject(utf8String)
    }
}