package com.example.deliver_food.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.deliver_food.R
import com.example.deliver_food.RestaurantsDetailsActivity
import com.example.deliver_food.database.RestauransEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(
    val context: Context,
    val itemList: List<RestauransEntity>
) : RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_all_restaurants_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        var restauransEntity = itemList[position]
        holder.txtRestaurantName.text = restauransEntity.restaurantName
        holder.txtCostPerPerson.text = restauransEntity.restaurantCostForOne
        holder.txtRatings.text = restauransEntity.restaurantRating
        Picasso.get().load(restauransEntity.restaurantImgUrl).error(R.drawable.default_image)
            .into(holder.imgRestaurantsImage)

        holder.llContent.setOnClickListener {

            val intent = Intent(context, RestaurantsDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restauransEntity.restaurant_id.toString())
            intent.putExtra("restaurant_name", restauransEntity.restaurantName)
            intent.putExtra("restaurant_rating", restauransEntity.restaurantRating)
            intent.putExtra("restaurant_cost_for_one", restauransEntity.restaurantCostForOne)
            intent.putExtra("restaurant_img_url", restauransEntity.restaurantImgUrl)
            context.startActivity(intent)
        }
        val restaurantsEntity = RestauransEntity(
            restauransEntity.restaurant_id.toInt(),
            restauransEntity.restaurantName,
            restauransEntity.restaurantRating,
            restauransEntity.restaurantCostForOne,
            restauransEntity.restaurantImgUrl
        )
        val checkFav =
            AllRestaurantsRecyclerAdapter.DBAsyncTask(context, restaurantsEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite_with_border)

        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite_without_border)
        }
        holder.imgFavourite.setOnClickListener {
            if (!AllRestaurantsRecyclerAdapter.DBAsyncTask(context, restaurantsEntity, 1).execute()
                    .get()
            ) {
                val async =
                    AllRestaurantsRecyclerAdapter.DBAsyncTask(context, restaurantsEntity, 2)
                        .execute()
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
                    AllRestaurantsRecyclerAdapter.DBAsyncTask(context, restaurantsEntity, 3)
                        .execute()
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
    }

    override fun getItemCount(): Int {

        return itemList.size
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgRestaurantsImage: ImageView = view.findViewById(R.id.imgRestaurantsImage)
        var txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        var txtCostPerPerson: TextView = view.findViewById(R.id.txtCostPerPerson)
        var imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        var txtRatings: TextView = view.findViewById(R.id.txtRatings)
        var llContent: LinearLayout = view.findViewById(R.id.llContent)
    }


}