package com.example.deliver_food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.deliver_food.adapter.CartRecyclerAdapter
import com.example.deliver_food.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtRestaurantName: TextView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerCart = findViewById(R.id.recyclerCart)

        layoutManager = LinearLayoutManager(this@CartActivity)

        txtRestaurantName = findViewById(R.id.txtRestaurantName)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"

        val userId = sharedPreferences.getString("user_id", "Username")

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE


        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        val foodNameList = intent.getStringArrayListExtra("food_name")
        val foodCost = intent.getStringArrayListExtra("food_cost")
        val restaurantName = intent.getStringExtra("restaurant_name")
        val foodIdList = intent.getStringArrayListExtra("food_id")
        val restaurantId = intent.getStringExtra("restaurant_id")

        txtRestaurantName.text = restaurantName
        var totalCost = 0
        for (i in foodCost) {
            totalCost += (i.toInt())
        }
        btnPlaceOrder.text = "Place Order(Total Rs. " + totalCost.toString() + ")"

        recyclerAdapter = CartRecyclerAdapter(this@CartActivity, foodNameList, foodCost)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        btnPlaceOrder.setOnClickListener {


            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val jsonParams = JSONObject()
            val jsonArray = JSONArray()
            for (i in foodIdList) {
                val foodItemIdParams = JSONObject()
                foodItemIdParams.put("food_item_id", i)
                jsonArray.put(foodItemIdParams)
            }
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", restaurantId)
            jsonParams.put("total_cost", totalCost.toString())
            jsonParams.put("food", jsonArray)


            if (ConnectionManager().checkConnectivity(this@CartActivity)) {


                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonParams,
                    com.android.volley.Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {

                                val intent =
                                    Intent(this@CartActivity, OrderConfirmedActivity::class.java)
                                startActivity(intent)


                            } else {
                                val data = it.getJSONObject("data")
                                val error = data.getString("errorMessage")
                                Toast.makeText(
                                    this@CartActivity,
                                    error, Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {

                            Toast.makeText(
                                this@CartActivity,
                                "Some unexpected error occurred! $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }, com.android.volley.Response.ErrorListener {
                        Toast.makeText(
                            this@CartActivity,
                            "Volley error $it!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4395bed9797ab2"
                        return headers

                    }

                }
                queue.add(jsonObjectRequest)
            } else {
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { text, listner ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit") { text, listner ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }

                dialog.create()
                dialog.show()
            }


        }
    }
}

