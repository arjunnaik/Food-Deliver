package com.example.deliver_food

import android.app.AlertDialog
import android.content.Intent
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
import com.example.deliver_food.adapter.AllRestaurantsRecyclerAdapter
import com.example.deliver_food.adapter.RestaurantsDetailsRecyclerAdapter
import com.example.deliver_food.database.RestauransEntity
import com.example.deliver_food.model.RestaurantsDetails
import com.example.deliver_food.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_all_restaurants.*
import org.json.JSONException

class RestaurantsDetailsActivity : AppCompatActivity() {

    var restaurantsDetailsList = ArrayList<RestaurantsDetails>()


    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var recyclerRestaurantsDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantsDetailsRecyclerAdapter
    lateinit var btnProceedToCart: Button
    lateinit var imgFavourite: ImageView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurants_details)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Food Items"
        recyclerRestaurantsDetails = findViewById(R.id.recyclerRestaurantsDetails)

        layoutManager = LinearLayoutManager(this@RestaurantsDetailsActivity)

        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        imgFavourite = findViewById(R.id.imgFavourite)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        val restaurantsId = intent.getStringExtra("restaurant_id")
        val restaurantsName = intent.getStringExtra("restaurant_name")

        val restaurantsRatings = intent.getStringExtra("restaurant_rating")
        val restaurantsCostForOne = intent.getStringExtra("restaurant_cost_for_one")
        val restaurantsImgUrl = intent.getStringExtra("restaurant_img_url")


        val restaurantsEntity = RestauransEntity(
            restaurantsId?.toInt() as Int,
            restaurantsName,
            restaurantsRatings,
            restaurantsCostForOne,
            restaurantsImgUrl
        )

        val checkFav =
            AllRestaurantsRecyclerAdapter.DBAsyncTask(applicationContext, restaurantsEntity, 1)
                .execute()
        val isFav = checkFav.get()
        if (isFav) {
            imgFavourite.setImageResource(R.drawable.ic_favourite_with_border)

        } else {
            imgFavourite.setImageResource(R.drawable.ic_favourite_without_border)
        }
        imgFavourite.setOnClickListener {
            if (!AllRestaurantsRecyclerAdapter.DBAsyncTask(applicationContext, restaurantsEntity, 1)
                    .execute()
                    .get()
            ) {
                val async =
                    AllRestaurantsRecyclerAdapter.DBAsyncTask(
                        applicationContext,
                        restaurantsEntity,
                        2
                    ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this@RestaurantsDetailsActivity,
                        "Restaurant added to favourites!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    imgFavourite.setImageResource(R.drawable.ic_favourite_with_border)
                } else {


                    Toast.makeText(
                        this@RestaurantsDetailsActivity,
                        "Some error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } else {
                val async =
                    AllRestaurantsRecyclerAdapter.DBAsyncTask(
                        applicationContext,
                        restaurantsEntity,
                        3
                    ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this@RestaurantsDetailsActivity,
                        "Restaurant removed from favourites!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    imgFavourite.setImageResource(R.drawable.ic_favourite_without_border)

                } else {

                    Toast.makeText(
                        this@RestaurantsDetailsActivity,
                        "Some error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        if (ConnectionManager().checkConnectivity(this@RestaurantsDetailsActivity)) {

            val queue = Volley.newRequestQueue(this@RestaurantsDetailsActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/" + restaurantsId
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET,
                    url,
                    null,
                    com.android.volley.Response.Listener
                    {

                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val data1 = data.getJSONArray("data")

                                progressLayout.visibility = View.GONE
                                progressBar.visibility = View.GONE
                                for (i in 0 until data1.length()) {
                                    val restaurantDetailsJsonObject = data1.getJSONObject(i)
                                    val restaurantDetailsObject = RestaurantsDetails(
                                        restaurantDetailsJsonObject.getString("id"),
                                        restaurantDetailsJsonObject.getString("name"),
                                        restaurantDetailsJsonObject.getString("cost_for_one"),
                                        restaurantDetailsJsonObject.getString("restaurant_id")

                                    )

                                    restaurantsDetailsList.add(restaurantDetailsObject)
                                }
                                recyclerAdapter = RestaurantsDetailsRecyclerAdapter(
                                    this@RestaurantsDetailsActivity,
                                    restaurantsDetailsList,
                                    btnProceedToCart,
                                    restaurantsName,
                                    restaurantsId
                                )

                                recyclerRestaurantsDetails.adapter = recyclerAdapter
                                recyclerRestaurantsDetails.layoutManager = layoutManager

                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@RestaurantsDetailsActivity,
                                "JSON Exception",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }, com.android.volley.Response.ErrorListener {
                        print("Error")
                    }

                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4395bed9797ab2"
                        return headers

                    }

                }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(this@RestaurantsDetailsActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@RestaurantsDetailsActivity)
            }

            dialog.create()
            dialog.show()
        }


    }


}



