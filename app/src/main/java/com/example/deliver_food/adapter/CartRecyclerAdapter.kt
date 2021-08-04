package com.example.deliver_food.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deliver_food.R

class CartRecyclerAdapter(
    val context: Context,
    val foodNameList: ArrayList<String>,
    val foodCost: ArrayList<String>
) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.txtFoodName.text = foodNameList[position]
        holder.txtCostPerFood.text = "Rs." + foodCost[position]
    }

    override fun getItemCount(): Int {
        return foodCost.size

    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        var txtCostPerFood: TextView = view.findViewById(R.id.txtCostPerFood)

    }


}


