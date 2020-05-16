package com.internshala.FoodRunnerApp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.view.menu.MenuAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.adapter.MenuRecyclerAdapter
import com.internshala.FoodRunnerApp.database.OrderDatabase
import com.internshala.FoodRunnerApp.database.OrderEntity
import com.internshala.FoodRunnerApp.model.Menu
import com.internshala.FoodRunnerApp.util.ConnectionManager
import org.json.JSONException

class MenuActivity : AppCompatActivity() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recycleAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar:Toolbar
    lateinit var btnProceed: Button
    var restaurantId:String?=""
    var restaurantName:String?=""

    var infoList=ArrayList<Menu>()
    var orderList= ArrayList<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        recyclerDashboard = findViewById(R.id.recyclerMenu)
        progressBar=findViewById(R.id.progressBar)
        progressLayout=findViewById(R.id.progressLayout)
        btnProceed=findViewById(R.id.btnProceed)

        progressLayout.visibility= View.VISIBLE

        restaurantId=intent.getStringExtra("id")
        restaurantName=intent.getStringExtra("name")

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layoutManager = LinearLayoutManager(this@MenuActivity)

        val queue = Volley.newRequestQueue(this@MenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        btnProceed.setOnClickListener {
            proceedToCart()
        }

        if (ConnectionManager().checkConnectivity(this@MenuActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    progressLayout.visibility=View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        for (i in 0 until resArray.length()) {
                            val jsonObject = resArray.getJSONObject(i)
                            val Object = Menu(
                                jsonObject.getString("id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("cost_for_one"),
                                jsonObject.getString("restaurant_id")
                            )

                            infoList.add(Object)
                        }

                            recycleAdapter = MenuRecyclerAdapter(this@MenuActivity,infoList,
                                object : MenuRecyclerAdapter.OnItemClickListener {
                                override fun onAddItemClick(menu:Menu) {
                                    orderList.add(menu)
                                    if (orderList.size > 0) {
                                        btnProceed.visibility = View.VISIBLE
                                    }
                                }

                                    override fun onRemoveItemClick(menu: Menu) {
                                        orderList.remove(menu)
                                        if (orderList.isEmpty()) {
                                            btnProceed.visibility = View.GONE
                                        }
                                    }
                                })

                            recyclerDashboard.adapter = recycleAdapter
                            recyclerDashboard.layoutManager = layoutManager
                        }
                    else {
                        Toast.makeText(
                            this@MenuActivity,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                catch(e: JSONException){
                    Toast.makeText(
                        this@MenuActivity,
                        "Some Unexpected Error Occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                    Toast.makeText(
                        this@MenuActivity,
                        "Volley Error Occurred!!",
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
        }

        else{
            val dialog = AlertDialog.Builder(this@MenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@MenuActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun proceedToCart() {

        val gson = Gson()
        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(this@MenuActivity,restaurantId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putString("resId", restaurantId)
            data.putString("resName",restaurantName)
            val intent = Intent(this@MenuActivity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
            recreate()
        } else
        {
            Toast.makeText(this@MenuActivity, "Some unexpected error", Toast.LENGTH_SHORT).show()
        }
    }

    class ItemsOfCart(context: Context, restaurantId: String, foodItems: String, private val mode: Int) : AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "res-db").build()

        private val orderEntity=OrderEntity(restaurantId, foodItems)

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(orderEntity)
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(orderEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
