package com.internshala.FoodRunnerApp.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.adapter.OrderHistoryAdapter
import com.internshala.FoodRunnerApp.model.OrderDetails
import com.internshala.FoodRunnerApp.util.ConnectionManager
import java.util.ArrayList

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrder: RecyclerView
    lateinit var recycleAdapter: OrderHistoryAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var rlNoOrders:RelativeLayout
    lateinit var rlHasOrders:RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences

    val orderHistoryList=ArrayList<OrderDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences =
            activity!!.getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null).toString()

        recyclerOrder = view.findViewById(R.id.recyclerOrder)
        progressLayout = view.findViewById(R.id.progressLayoutOrder)
        progressBar = view.findViewById(R.id.progressBarOrder)
        rlNoOrders=view.findViewById(R.id.rlNoOrder)
        rlHasOrders=view.findViewById(R.id.rlHasOrders)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    progressLayout.visibility = View.GONE
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            if (resArray.length() == 0) {
                                rlHasOrders.visibility = View.GONE
                                rlNoOrders.visibility = View.VISIBLE
                            } else {
                                for (i in 0 until resArray.length()) {
                                    val orderObject = resArray.getJSONObject(i)
                                    val foodItems = orderObject.getJSONArray("food_items")
                                    val orderDetails = OrderDetails(
                                        orderObject.getInt("order_id"),
                                        orderObject.getString("restaurant_name"),
                                        orderObject.getString("order_placed_at"),
                                        foodItems
                                    )
                                    orderHistoryList.add(orderDetails)
                                    if (orderHistoryList.isEmpty()) {
                                        rlHasOrders.visibility = View.GONE
                                        rlNoOrders.visibility = View.VISIBLE
                                    } else {
                                        rlHasOrders.visibility = View.VISIBLE
                                        rlNoOrders.visibility = View.GONE
                                        if (activity != null) {
                                            recycleAdapter = OrderHistoryAdapter(
                                                activity as Context,
                                                orderHistoryList
                                            )
                                            val mLayoutManager =
                                                LinearLayoutManager(activity as Context)
                                            recyclerOrder.layoutManager = mLayoutManager
                                            recyclerOrder.itemAnimator = DefaultItemAnimator()
                                            recyclerOrder.adapter = recycleAdapter
                                        } else {
                                            queue.cancelAll(this::class.java.simpleName)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }
}

