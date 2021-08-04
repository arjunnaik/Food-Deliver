package com.example.deliver_food.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantsDao {
    @Insert
    fun insertBook(restauransEntity: RestauransEntity)

    @Delete
    fun deleteBook(restauransEntity: RestauransEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<RestauransEntity>

    @Query("SELECT * FROM restaurants WHERE restaurant_id = :restaurantId")
    fun getRestaurantById(restaurantId: String): RestauransEntity
}



