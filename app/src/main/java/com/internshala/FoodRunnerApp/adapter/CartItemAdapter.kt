package com.internshala.FoodRunnerApp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.model.FoodItem
import com.internshala.FoodRunnerApp.model.Menu

class CartItemAdapter(val context: Context, private val itemList:ArrayList<FoodItem>): RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)
        return CartItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val menu = itemList[position]
        holder.txtCartItem.text = menu.name
        holder.txtCartPrice.text = menu.cost.toString()
    }

    class CartItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtCartPrice: TextView =view.findViewById(R.id.txtCartPrice)
        val txtCartItem: TextView =view.findViewById(R.id.txtCartItem)
    }
}