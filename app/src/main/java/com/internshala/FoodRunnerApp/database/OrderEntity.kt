package com.internshala.FoodRunnerApp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val restaurant_id:String,
    @ColumnInfo(name = "foodItems") val foodItems: String
)