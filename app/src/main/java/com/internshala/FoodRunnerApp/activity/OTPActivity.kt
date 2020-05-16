package com.internshala.FoodRunnerApp.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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

class OTPActivity : AppCompatActivity() {

    lateinit var etOTP:EditText
    lateinit var etPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var btnSubmit:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        etOTP=findViewById(R.id.etOTP)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnSubmit=findViewById(R.id.btnSubmit)

        val mobile=intent.getStringExtra("mobile")
        //System.out.println(mobile)

        btnSubmit.setOnClickListener {

            val otp=etOTP.text.toString()
            val password=etPassword.text.toString()
            val confirmPassword=etConfirmPassword.text.toString()

            if (otp == "" || otp.length!=4)
            {
                etOTP.error = "Invalid OTP"
                etOTP.requestFocus()
            }
            else if(password == "" && password.length<4)
            {
                etPassword.error = "Invalid Password"
                etPassword.requestFocus()
            }
            else if(confirmPassword!=password)
            {
                etConfirmPassword.error="Incorrect Password"
                etConfirmPassword.requestFocus()
            }
            else
            {
                if (ConnectionManager().checkConnectivity(this@OTPActivity)) {

                    val queue = Volley.newRequestQueue(this@OTPActivity)
                    val url="http://13.235.250.119/v2/reset_password/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number",mobile)
                    jsonParams.put("password",password)
                    jsonParams.put("otp",otp)

                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST,url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {

                                        Toast.makeText(
                                            this@OTPActivity,"Password Changed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        startActivity(
                                            Intent(
                                                this@OTPActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@OTPActivity,"Invalid OTP",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@OTPActivity,"Some Error Occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                //Log.e("Error::::", "/post request fail! Error: ${it.message}")
                                Toast.makeText(
                                    this@OTPActivity,"Volley Error Occurred",
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
                    val dialog = AlertDialog.Builder(this@OTPActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@OTPActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

    }
}
