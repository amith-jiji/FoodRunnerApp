package com.internshala.FoodRunnerApp.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotMobile: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnForgotNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        etForgotMobile = findViewById(R.id.etForgotMobile)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnForgotNext = findViewById(R.id.btnForgotNext)

        btnForgotNext.setOnClickListener {

            val email=etForgotEmail.text.toString()
            val mobile=etForgotMobile.text.toString()

            if (mobile == "" || mobile.length!=10)
            {
                etForgotMobile.error = "Invalid Mobile Number"
                etForgotMobile.requestFocus()
            }
            else if(email == "")
            {
                etForgotEmail.error = "Invalid Email"
                etForgotEmail.requestFocus()
            }
            else
            {
                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

                    val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                    val url="http://13.235.250.119/v2/forgot_password/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number",mobile)
                    jsonParams.put("email", email)

                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST,url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val intent=Intent(
                                            this@ForgotPasswordActivity,
                                            OTPActivity::class.java)
                                        intent.putExtra("mobile",mobile)
                                        startActivity(intent)
                                        //finish()
                                    } else {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,"Invalid Credentials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,"Some Error Occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                //Log.e("Error::::", "/post request fail! Error: ${it.message}")
                                Toast.makeText(
                                    this@ForgotPasswordActivity,"Volley Error Occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "6e70d9ed4fe682"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)

                } else{
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}
