package com.example.deliver_food.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestauransEntity::class], version = 1)
abstract class RestaurantsDatabase : RoomDatabase() {
    abstract fun restaurantsDao(): RestaurantsDao
}



