package com.internshala.FoodRunnerApp.adapter

import android.content.Context
import android.os.AsyncTask
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.database.FoodDatabase
import com.internshala.FoodRunnerApp.database.FoodEntity
import com.internshala.FoodRunnerApp.database.OrderDatabase
import com.internshala.FoodRunnerApp.database.OrderEntity
import com.internshala.FoodRunnerApp.model.Food
import com.internshala.FoodRunnerApp.model.Menu

class MenuRecyclerAdapter(val context: Context, private val itemList:ArrayList<Menu>,private val listener: OnItemClickListener): RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtId: TextView =view.findViewById(R.id.txtId)
        val txtPrice: TextView =view.findViewById(R.id.txtPrice)
        val txtItem: TextView =view.findViewById(R.id.txtItem)
        val btnAdd: Button =view.findViewById(R.id.btnAdd)
        val btnRemove:Button=view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_single_row,parent,false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(menu:Menu)
        fun onRemoveItemClick(menu: Menu)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = itemList[position]
        holder.txtId.text = (position + 1).toString()
        holder.txtPrice.text = menu.price
        holder.txtItem.text = menu.name

        holder.btnAdd.setOnClickListener {
            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE
            listener.onAddItemClick(menu)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.onRemoveItemClick(menu)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}