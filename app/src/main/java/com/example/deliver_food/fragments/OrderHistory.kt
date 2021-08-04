package com.example.deliver_food.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.deliver_food.R
import com.example.deliver_food.adapter.OrderHistoryRecyclerAdapter
import com.example.deliver_food.util.ConnectionManager
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    lateinit var recycyclerRestaurantName: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val resName = ArrayList<String>()
    val orderDate = ArrayList<String>()


    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        recycyclerRestaurantName = view.findViewById(R.id.recycyclerRestaurantName)

        layoutManager = LinearLayoutManager(activity)
        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE
        )
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        val userId = sharedPreferences.getString("user_id", "")

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/" + userId
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null, com.android.volley.Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val data1 = data.getJSONArray("data")
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            for (i in 0 until data1.length()) {
                                val restaurantJsonObject = data1.getJSONObject(i)
                                resName.add(restaurantJsonObject.getString("restaurant_name"))
                                orderDate.add(restaurantJsonObject.getString("order_placed_at"))


                            }
                            recyclerAdapter = OrderHistoryRecyclerAdapter(
                                activity as Context,
                                resName,
                                orderDate
                            )
                            recycyclerRestaurantName.adapter = recyclerAdapter
                            recycyclerRestaurantName.layoutManager = layoutManager


                        }
                    } catch (e: JSONException) {
                        Toast.makeText(context, "JSON Exception", Toast.LENGTH_LONG).show()
                    }
                }, com.android.volley.Response.ErrorListener {
                    Toast.makeText(context, "Volley Error Occurred $it", Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity!!)
            }

            dialog.create()
            dialog.show()
        }




        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


