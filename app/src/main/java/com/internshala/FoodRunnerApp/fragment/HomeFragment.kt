package com.internshala.FoodRunnerApp.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.adapter.HomeRecyclerAdapter
import com.internshala.FoodRunnerApp.model.Food
import com.internshala.FoodRunnerApp.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recycleAdapter: HomeRecyclerAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar

    var foodInfoList=ArrayList<Food>()

    var ratingComparator=Comparator<Food>{food1,food2 ->
        if(food1.foodRating.compareTo(food2.foodRating,true)==0)
            food1.foodName.compareTo(food2.foodName,true)
        else
            food1.foodRating.compareTo(food2.foodRating,true)
    }

    var nameComparator=Comparator<Food>{food1,food2 ->
        if(food1.foodName.compareTo(food2.foodName,true)==0)
            food1.foodRating.compareTo(food2.foodRating,true)
        else
            food1.foodName.compareTo(food2.foodName,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)
        recyclerDashboard = view.findViewById(R.id.recyclerHome)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    progressLayout.visibility=View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        for (i in 0 until resArray.length()) {
                            val foodJsonObject = resArray.getJSONObject(i)
                            val foodObject = Food(
                                foodJsonObject.getString("id"),
                                foodJsonObject.getString("name"),
                                foodJsonObject.getString("rating"),
                                foodJsonObject.getString("cost_for_one"),
                                foodJsonObject.getString("image_url")
                            )
                            foodInfoList.add(foodObject)

                            recycleAdapter = HomeRecyclerAdapter(activity as Context, foodInfoList)
                            recyclerDashboard.adapter = recycleAdapter
                            recyclerDashboard.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                catch(e:JSONException){
                    Toast.makeText(
                        activity as Context,
                        "Some Unexpected Error Occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {

                if(activity!=null) {
                    Toast.makeText(
                        activity as Context,
                        "Volley Error Occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if(id==R.id.action_sort)
        {
            val groupName= arrayOf("Ratings (High to Low)","Ratings (Low to High)","Restaurant Name")
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By")
            dialog.setSingleChoiceItems(
                groupName,
                -1
            ) { _, item ->
                when (item) {
                    0 -> {
                        Collections.sort(foodInfoList,ratingComparator)
                        foodInfoList.reverse()
                    }
                    1 -> Collections.sort(foodInfoList,ratingComparator)
                    2->  Collections.sort(foodInfoList,nameComparator)
                }
            }
            dialog.setPositiveButton("OK") { _, _ ->
                recycleAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("Exit") { _, _ ->

            }
            dialog.create()
            dialog.show()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
    }
}




