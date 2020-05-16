package com.internshala.FoodRunnerApp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var txtRegisterYourself: TextView
    private lateinit var btnLogin: Button
    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtForgotPassword: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLogged", false)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)

        sharedPreferences=getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        txtRegisterYourself.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val mobile=etMobileNumber.text.toString()
            val password=etPassword.text.toString()

            if (mobile == "" || mobile.length!=10)
            {
                etMobileNumber.error = "Invalid Mobile Number"
                etMobileNumber.requestFocus()
            }
            else if(password == "")
            {
                etPassword.error = "Invalid Password"
                etPassword.requestFocus()
            }

            else
            {
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    val url="http://13.235.250.119/v2/login/fetch_result/"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etMobileNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())

                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST,url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val response = data.getJSONObject("data")
                                        sharedPreferences.edit()
                                            .putString("user_id", response.getString("user_id"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_name", response.getString("name"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString(
                                                "user_mobile_number",
                                                response.getString("mobile_number")
                                            )
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString(
                                                "user_address",
                                                response.getString("address")
                                            )
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_email", response.getString("email"))
                                            .apply()
                                        sharedPreferences.edit().putBoolean("isLogged",true).apply()
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,"Invalid Credentials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            },
                            Response.ErrorListener {
                                Log.e("Error::::", "/post request fail! Error: ${it.message}")
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
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}

