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

class HomeRecyclerAdapter(val context: Context, private val itemList:ArrayList<Food>):RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food = itemList[position]
        holder.txtFoodName.text = food.foodName
        holder.txtFoodPrice.text = food.foodPrice
        holder.txtFoodRating.text = food.foodRating
        Picasso.get().load(food.foodImage).error(R.drawable.default_image).into(holder.imgFoodImage)

        val foodEntity = FoodEntity(
            food.foodId?.toInt() as Int,
            food.foodName,
            food.foodRating,
            food.foodPrice,
            food.foodImage
        )

        val checkFav = DBAsyncTask(context, foodEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFavourite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_favorite_border)
        }

        holder.imgFavourite.setOnClickListener {

            if (!DBAsyncTask(context, foodEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, foodEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.imgFavourite.setImageResource(R.drawable.ic_favorite_filled)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val async = DBAsyncTask(context,foodEntity, 3).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()

                    holder.imgFavourite.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.llcontent.setOnClickListener {
                val intent= Intent(context, MenuActivity::class.java)
                intent.putExtra("id",food.foodId)
                intent.putExtra("name",food.foodName)
                context.startActivity(intent)
        }
    }

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtFoodName:TextView=view.findViewById(R.id.txtFoodName)
        val txtFoodPrice:TextView=view.findViewById(R.id.txtFoodPrice)
        val txtFoodRating:TextView=view.findViewById(R.id.txtFoodRating)
        val imgFoodImage:ImageView=view.findViewById(R.id.imgFoodImage)
        val imgFavourite:ImageView=view.findViewById(R.id.imgFavourite)
        val llcontent:LinearLayout=view.findViewById(R.id.llContent)
    }

    class DBAsyncTask(val context: Context, private val foodEntity: FoodEntity, private val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    // Check DB if the book is favourite or not
                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.food_id.toString())
                    db.close()
                    return food != null

                }

                2 -> {

                    // Save the book into DB as favourite
                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true

                }

                3 -> {

                    // Remove the favourite book
                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }
}
