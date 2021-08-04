package com.example.deliver_food.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.deliver_food.CartActivity
import com.example.deliver_food.R
import com.example.deliver_food.model.RestaurantsDetails

class RestaurantsDetailsRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<RestaurantsDetails>,
    val button: Button,
    val restaurantName: String,
    val restaurantId: String
) : RecyclerView.Adapter<RestaurantsDetailsRecyclerAdapter.RestaurantsDetailsViewHolder>() {


    val foodNameList = ArrayList<String>()
    val foodCost = ArrayList<String>()
    val foodIdList = ArrayList<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantsDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurants_details_single_row, parent, false)
        return RestaurantsDetailsViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RestaurantsDetailsViewHolder, position: Int) {
        val restaurantsDetails = itemList[position]
        holder.txtFoodName.text = restaurantsDetails.foodName
        holder.txtCostPerFood.text = "Rs. " + restaurantsDetails.foodCost
        holder.txtCounter.text = (position + 1).toString()

        holder.btnAddToCart.setOnClickListener {
            var check = false

            for (i in foodNameList) {
                if (i == restaurantsDetails.foodName) {
                    check = true
                    foodNameList.remove(restaurantsDetails.foodName)
                    foodIdList.remove(restaurantsDetails.restaurantsDetailsId)
                    foodCost.remove(restaurantsDetails.foodCost)
                    val color = ContextCompat.getColor(context, R.color.redColor)
                    holder.btnAddToCart.setBackgroundColor(color)
                    holder.btnAddToCart.text = "Add"
                    break

                }
            }
            if (!check) {
                val color = ContextCompat.getColor(context, R.color.yellowColor)
                holder.btnAddToCart.setBackgroundColor(color)
                holder.btnAddToCart.text = "Remove"
                foodNameList.add(restaurantsDetails.foodName)
                foodCost.add(restaurantsDetails.foodCost)
                foodIdList.add(restaurantsDetails.restaurantsDetailsId)
            }

            if (foodNameList.size >= 1) {
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    val intent = Intent(context, CartActivity::class.java)

                    intent.putExtra("food_name", foodNameList)
                    intent.putExtra("food_cost", foodCost)
                    intent.putExtra("restaurant_name", restaurantName)
                    intent.putExtra("food_id", foodIdList)
                    intent.putExtra("restaurant_id", restaurantId)
                    context.startActivity(intent)
                }

            } else {
                button.visibility = View.GONE
            }


        }

    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    class RestaurantsDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        var txtCounter: TextView = view.findViewById(R.id.txtCounter)
        var txtCostPerFood: TextView = view.findViewById(R.id.txtCostPerFood)
        var btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)


    }
}