package com.internshala.FoodRunnerApp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.util.ConnectionManager
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var btnRegister: Button
    lateinit var etName: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etAddress: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var rlRegister: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)

        rlRegister = findViewById(R.id.rlRegister)
        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val password = etPassword.text.toString()
            val phone = etPhoneNumber.text.toString()

            if (name == "") {
                etName.error = "Invalid Name"
                etName.requestFocus()
            }
            else if (email == "") {
                etEmail.error = "Invalid Email"
                etEmail.requestFocus()
            }
            else if (phone == "" || phone.length!=10) {
                etPhoneNumber.error = "Invalid Phone Number"
                etPhoneNumber.requestFocus()
            }
            else if (address == "" || address.length<=10) {
                etAddress.error = "Invalid Address"
                etAddress.requestFocus()
            }
            else if (password == "" || password.length<4) {
                etPassword.error = "Invalid Password"
                etPassword.requestFocus()
            }
            else if (confirmPassword != password) {
                etConfirmPassword.error = "Incorrect Password"
                etConfirmPassword.requestFocus()
            }
            else {
                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {

                    val queue = Volley.newRequestQueue(this@RegisterActivity)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", phone)
                    jsonParams.put("password", password)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_mobile_number",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email"))
                                        .apply()
                                    sharedPreferences.edit().putBoolean("isLogged", true).apply()
                                    startActivity(
                                        Intent(
                                            this@RegisterActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Some Error Occurred!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(this@RegisterActivity,"Volley Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "6e70d9ed4fe682"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }
                else{
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}