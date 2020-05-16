package com.internshala.FoodRunnerApp.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.adapter.CartRecyclerAdapter
import com.internshala.FoodRunnerApp.adapter.FavouriteRecyclerAdapter
import com.internshala.FoodRunnerApp.database.FoodEntity
import com.internshala.FoodRunnerApp.database.OrderDatabase
import com.internshala.FoodRunnerApp.database.OrderEntity
import com.internshala.FoodRunnerApp.model.Food
import com.internshala.FoodRunnerApp.model.Menu
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recylerCart:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var btnPlaceOrder:Button
    lateinit var txtResName:TextView
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences

    private var restaurantId: String? = ""
    private var restaurantName: String? = ""

    var orderList = ArrayList<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recylerCart=findViewById(R.id.recyclerCart)
        btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
        txtResName=findViewById(R.id.txtResName)
        layoutManager=LinearLayoutManager(this@CartActivity)
        sharedPreferences = getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)

        val data = intent.getBundleExtra("data")
        restaurantId = data.getString("resId")
        restaurantName = data.getString("resName")

        toolbar = findViewById(R.id.toolbarCart)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtResName.text=restaurantName

        val dbList = GetItemsFromDBAsync(applicationContext).execute().get()
        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<Menu>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            Toast.makeText(this@CartActivity, "Empty List", Toast.LENGTH_SHORT).show()
        }

        recyclerAdapter=CartRecyclerAdapter(this@CartActivity,orderList)
        recyclerCart.adapter=recyclerAdapter
        recyclerCart.layoutManager=layoutManager

        var sum:Int= 0
        for (i in 0 until orderList.size) {
            sum += Integer.parseInt(orderList[i].price)
        }
        val total = "Place Order (Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {

            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonParams = JSONObject()
            jsonParams.put("user_id",sharedPreferences.getString("user_id", null).toString())

            jsonParams.put("restaurant_id", restaurantId.toString())

            var sum = 0
            for (i in 0 until orderList.size) {
                sum += Integer.parseInt(orderList[i].price)
            }

            jsonParams.put("total_cost", sum.toString())

            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", orderList[i].id)
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            ClearDBAsync(applicationContext, restaurantId.toString()).execute().get()

                            val dialog = Dialog(
                                this@CartActivity,
                                android.R.style.Theme_Black_NoTitleBar_Fullscreen
                            )
                            dialog.setContentView(R.layout.activity_confirm)
                            dialog.show()
                            dialog.setCancelable(false)
                            val btnOk = dialog.findViewById<Button>(R.id.btnOK)
                            btnOk.setOnClickListener {
                                dialog.dismiss()
                                startActivity(Intent(this@CartActivity, MainActivity::class.java))
                                ActivityCompat.finishAffinity(this@CartActivity)
                            }
                        } else {
                            Toast.makeText(this@CartActivity, "Couldn't Place The Order", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception)
                    {
                        Toast.makeText(this@CartActivity,"Some Error Occurred!!", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] =  "6e70d9ed4fe682"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)

        }
    }

    class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }

    }

    class ClearDBAsync(context: Context, private val resId: String) :
        AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, OrderDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }

    override fun onBackPressed() {
        val confirm = ClearDBAsync(applicationContext, restaurantId.toString()).execute().get()
        if (confirm){
            Toast.makeText(
                this@CartActivity,
                "Cart Cleared",
                Toast.LENGTH_SHORT
            ).show()
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
