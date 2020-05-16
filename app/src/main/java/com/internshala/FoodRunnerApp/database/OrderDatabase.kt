package com.internshala.FoodRunnerApp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class], version = 1)
abstract class OrderDatabase: RoomDatabase() {

    abstract fun orderDao(): OrderDao

}