package com.example.deliver_food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.deliver_food.util.ConnectionManager
import org.json.JSONObject

class LogInActivity : AppCompatActivity() {
    lateinit var tvForgotPassword: TextView
    lateinit var tvRegisterYourself: TextView
    lateinit var btnLogin: Button
    lateinit var etPassword: EditText
    lateinit var etMobileNumber: EditText


    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)


        if (isLoggedIn) {
            val intent = Intent(this@LogInActivity, MainActivity::class.java)
            startActivity(intent)
        }



        title = "Login"

        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        tvRegisterYourself = findViewById(R.id.tvRegisterYourself)

        btnLogin = findViewById(R.id.btnLogin)

        etMobileNumber = findViewById(R.id.etMobileNumber)

        etPassword = findViewById(R.id.etPassword)

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this@LogInActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        tvRegisterYourself.setOnClickListener {
            val intent = Intent(this@LogInActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            if (etMobileNumber.text.toString().length < 10) {
                etMobileNumber.error = "Please enter a valid phone number!"
            } else if (etPassword.text.toString().length < 4) {
                etPassword.error = "Minimum 4 character"
            } else {

                val queue = Volley.newRequestQueue(this@LogInActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", etMobileNumber.text.toString())
                jsonParams.put("password", etPassword.text.toString())


                if (ConnectionManager().checkConnectivity((this@LogInActivity))) {


                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        url,
                        jsonParams,
                        com.android.volley.Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {


                                    //   progressLayout.visibility = View.GONE
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    val intent =
                                        Intent(this@LogInActivity, MainActivity::class.java)
                                    startActivity(intent)


                                } else {
                                    val data = it.getJSONObject("data")

                                    val error = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@LogInActivity,
                                        error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {

                                Toast.makeText(
                                    this@LogInActivity,
                                    "Some unexpected error occurred! $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, com.android.volley.Response.ErrorListener {
                            Toast.makeText(
                                this@LogInActivity,
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
                    val dialog = AlertDialog.Builder(this@LogInActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listner ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        finish()

                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        ActivityCompat.finishAffinity(this@LogInActivity)
                    }

                    dialog.create()
                    dialog.show()
                }

            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}

//
//if ((sharedPreferences.getString(
//"MobileNumberRegistered",
//"hi"
//) == etMobileNumber.text.toString()) && (sharedPreferences.getString(
//"PasswordRegistered",
//"hi"
//) == etPassword.text.toString())
//) {
//    val intent = Intent(this@LogInActivity, GreetingActivity::class.java)
//    sharedPreferences.edit().putString("Mobile", etMobileNumber.text.toString()).apply()
//    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
//
//    startActivity(intent)
//} else {
//
//    Toast.makeText(this@LogInActivity, "Register Yourself First", Toast.LENGTH_SHORT).show()
//}
