package com.example.deliver_food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.deliver_food.util.ConnectionManager
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        title = "Register Yourself"

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)





        btnRegister.setOnClickListener {
            if (etName.text.toString().length < 3) {
                etName.error = "Minimum 3 character"
            } else if (etEmail.text.toString().length == 0) {
                etEmail.error = "This field should not be empty!"
            } else if (etMobileNumber.text.toString().length < 10) {
                etMobileNumber.error = "Please enter a valid phone number!"
            } else if (etDeliveryAddress.text.toString().length == 0) {
                etDeliveryAddress.error = "This field should not be empty!"
            } else if (etPassword.text.toString().length < 4) {
                etPassword.error = "Minimum 4 character"
            } else if (etConfirmPassword.text.toString().length < 4) {
                etConfirmPassword.error = "Minimum 4 character"
            } else if (etPassword.text.toString() != etConfirmPassword.text.toString()) {
                etConfirmPassword.error = "Password should match!"

            } else {

                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val mobNum = etMobileNumber.text.toString()
                val delAdd = etDeliveryAddress.text.toString()
                val password = etPassword.text.toString()


                val queue = Volley.newRequestQueue(this@RegistrationActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", mobNum)
                jsonParams.put("password", password)
                jsonParams.put("address", delAdd)
                jsonParams.put("email", email)


                if (ConnectionManager().checkConnectivity((this@RegistrationActivity))) {


                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        url,
                        jsonParams,
                        com.android.volley.Response.Listener {
                            print("response is $it")
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val data1 = data.getJSONObject("data")
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Registered Successfully!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sharedPreferences = getSharedPreferences(
                                        getString(R.string.preferences_file_name),
                                        Context.MODE_PRIVATE
                                    )
                                    sharedPreferences.edit()
                                        .putString("user_id", data1.getString("user_id")).apply()
                                    sharedPreferences.edit().putString("name", name).apply()
                                    sharedPreferences.edit()
                                        .putString("mobile_number", "+91-" + mobNum).apply()
                                    sharedPreferences.edit().putString("email", email).apply()
                                    sharedPreferences.edit().putString("address", delAdd).apply()
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                    val intent =
                                        Intent(this@RegistrationActivity, MainActivity::class.java)
                                    startActivity(intent)


                                } else {
                                    val data = it.getJSONObject("data")
                                    val error = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        error, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {

                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Some unexpected error occurred! $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, com.android.volley.Response.ErrorListener {
                            Toast.makeText(
                                this@RegistrationActivity,
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
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listner ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        finish()

                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        ActivityCompat.finishAffinity(this@RegistrationActivity)
                    }

                    dialog.create()
                    dialog.show()
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@RegistrationActivity, LogInActivity::class.java)
        startActivity(intent)
    }
}