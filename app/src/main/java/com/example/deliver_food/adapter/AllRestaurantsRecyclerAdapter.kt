package com.example.deliver_food.adapter

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
import com.example.deliver_food.R
import com.example.deliver_food.RestaurantsDetailsActivity
import com.example.deliver_food.database.RestauransEntity
import com.example.deliver_food.database.RestaurantsDatabase
import com.example.deliver_food.model.Restaurants
import com.squareup.picasso.Picasso

class AllRestaurantsRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<AllRestaurantsRecyclerAdapter.AllRestaurantsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRestaurantsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_all_restaurants_single_row, parent, false)
        return AllRestaurantsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllRestaurantsViewHolder, position: Int) {
        val restaurants = itemList[position]
        holder.txtRestaurantName.text = restaurants.restaurantName

        holder.txtCostPerPerson.text = restaurants.restaurantCostForOne + "/Person"
        holder.txtRatings.text = restaurants.restaurantRating
        Picasso.get().load(restaurants.restaurantImgUrl).error(R.drawable.default_image)
            .into(holder.imgRestaurantsImage)

        val restaurantsEntity = RestauransEntity(
            restaurants.restaurantId.toInt(),
            restaurants.restaurantName,
            restaurants.restaurantRating,
            restaurants.restaurantCostForOne + "/Person",
            restaurants.restaurantImgUrl

        )
        val checkFav = DBAsyncTask(context, restaurantsEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite_with_border)

        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite_without_border)
        }
        holder.imgFavourite.setOnClickListener {
            if (!DBAsyncTask(context, restaurantsEntity, 1).execute()
                    .get()
            ) {
                val async =
                    DBAsyncTask(context, restaurantsEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_favourite_with_border)
                } else {


                    Toast.makeText(
                        context,
                        "Some error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } else {
                val async =
                    DBAsyncTask(context, restaurantsEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_favourite_without_border)

                } else {

                    Toast.makeText(
                        context,
                        "Some error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
        holder.llContent.setOnClickListener {

            val intent = Intent(context, RestaurantsDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurants.restaurantId)
            intent.putExtra("restaurant_name", restaurants.restaurantName)
            intent.putExtra("restaurant_rating", restaurants.restaurantRating)
            intent.putExtra("restaurant_cost_for_one", restaurants.restaurantCostForOne)
            intent.putExtra("restaurant_img_url", restaurants.restaurantImgUrl)
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    class AllRestaurantsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        var imgRestaurantsImage: ImageView = view.findViewById(R.id.imgRestaurantsImage)
        var txtCostPerPerson: TextView = view.findViewById(R.id.txtCostPerPerson)
        var txtRatings: TextView = view.findViewById(R.id.txtRatings)
        var llContent: LinearLayout = view.findViewById(R.id.llContent)
        var imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)

    }

    class DBAsyncTask(
        val context: Context,
        val restaurantsEntity: RestauransEntity,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {


        val db =
            Room.databaseBuilder(context, RestaurantsDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val restaurants: RestauransEntity = db.restaurantsDao()
                        .getRestaurantById(restaurantsEntity.restaurant_id.toString())
                    db.close()
                    return restaurants != null
                }
                2 -> {
                    db.restaurantsDao().insertBook(restaurantsEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantsDao().deleteBook(restaurantsEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}