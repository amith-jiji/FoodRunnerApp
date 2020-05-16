package com.internshala.FoodRunnerApp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.activity.MenuActivity
import com.internshala.FoodRunnerApp.database.FoodDatabase
import com.internshala.FoodRunnerApp.database.FoodEntity
import com.internshala.FoodRunnerApp.model.Food
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, private val itemList:MutableList<FoodEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val food = itemList[position]
        holder.txtFoodName.text = food.foodName
        holder.txtFoodPrice.text = food.foodPrice
        holder.txtFoodRating.text = food.foodRating
        Picasso.get().load(food.foodImage).error(R.drawable.default_image).into(holder.imgFoodImage)
        holder.imgFavourite.setImageResource(R.drawable.ic_favorite_filled)

        val foodEntity = FoodEntity(
            food.food_id,
            food.foodName,
            food.foodRating,
            food.foodPrice,
            food.foodImage
        )

        holder.imgFavourite.setOnClickListener {
            val async = DBAsyncTask(context, foodEntity, 3).execute()
            val result = async.get()

            if (result) {

                holder.imgFavourite.setImageResource(R.drawable.ic_favorite_border)

                Toast.makeText(
                    context,
                    "Restaurant removed from favourites",
                    Toast.LENGTH_SHORT
                ).show()

                itemList.removeAt(position)
                notifyDataSetChanged()
            } else {
                Toast.makeText(
                    context,
                    "Some error occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.llcontent.setOnClickListener {
            val intent= Intent(context, MenuActivity::class.java)
            intent.putExtra("id",food.food_id.toString())
            intent.putExtra("name",food.foodName)
            context.startActivity(intent)
        }

    }

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtFoodName: TextView =view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView =view.findViewById(R.id.txtFoodPrice)
        val txtFoodRating: TextView =view.findViewById(R.id.txtFoodRating)
        val imgFoodImage: ImageView =view.findViewById(R.id.imgFoodImage)
        val imgFavourite: ImageView =view.findViewById(R.id.imgFavourite)
        val llcontent: LinearLayout =view.findViewById(R.id.llContent)
    }

    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {


        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.food_id.toString())
                    db.close()
                    return food != null

                }

                2 -> {

                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true

                }

                3 -> {

                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }
}