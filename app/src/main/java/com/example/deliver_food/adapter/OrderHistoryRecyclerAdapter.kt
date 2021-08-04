package com.example.deliver_food.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.deliver_food.R
import com.example.deliver_food.util.ConnectionManager
import org.json.JSONException

class OrderHistoryRecyclerAdapter(
    val context: Context,
    val resName: ArrayList<String>,
    val date: ArrayList<String>
) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    lateinit var sharedPreferences: SharedPreferences
    val foodName = ArrayList<String>()
    val orderDate = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.txtDate.text = date[position]
        holder.txtRestaurantName.text = resName[position]
        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartRecyclerAdapter

        if (ConnectionManager().checkConnectivity(context)) {

            try {


                sharedPreferences =
                    context.getSharedPreferences("Food Runner Preferences", Context.MODE_PRIVATE)

                val user_id = sharedPreferences.getString("user_id", "")

                val queue = Volley.newRequestQueue(context)

                val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            val fetechedRestaurantJsonObject = data.getJSONObject(position)


                            val foodOrderedJsonArray =
                                fetechedRestaurantJsonObject.getJSONArray("food_items")

                            for (j in 0 until foodOrderedJsonArray.length()) {
                                val eachFoodItem = foodOrderedJsonArray.getJSONObject(j)
                                foodName.add(eachFoodItem.getString("name"))
                                orderDate.add(eachFoodItem.getString("cost"))
                            }

                            orderedItemAdapter = CartRecyclerAdapter(
                                context,
                                foodName, orderDate
                            )

                            holder.recyclerFoodItems.adapter =
                                orderedItemAdapter

                            holder.recyclerFoodItems.layoutManager = layoutManager


                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "e168e28418726a"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)


            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }

    override fun getItemCount(): Int {
        return resName.size

    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
        var recyclerFoodItems: RecyclerView = view.findViewById(R.id.recyclerFoodItems)
    }

}




