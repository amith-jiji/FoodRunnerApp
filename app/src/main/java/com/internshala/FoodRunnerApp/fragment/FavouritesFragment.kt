package com.internshala.FoodRunnerApp.fragment


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.adapter.FavouriteRecyclerAdapter
import com.internshala.FoodRunnerApp.database.FoodDatabase
import com.internshala.FoodRunnerApp.database.FoodEntity

class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recycleAdapter: FavouriteRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var dbBookList = listOf<FoodEntity>()
    var dbList= mutableListOf<FoodEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerHome)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity as Context)

        dbBookList=RetriveFavourites(activity as Context).execute().get()
        dbList.addAll(dbBookList)

        if(activity!=null){
            progressLayout.visibility=View.GONE
            recycleAdapter=FavouriteRecyclerAdapter(activity as Context,dbList)
            recyclerFavourite.adapter=recycleAdapter
            recyclerFavourite.layoutManager=layoutManager
        }

        return view
    }

    class RetriveFavourites(val context: Context) : AsyncTask<Void, Void, List<FoodEntity>>() {
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context,FoodDatabase::class.java, "food-db").build()
            return db.foodDao().getAllFoods()
        }
    }
}