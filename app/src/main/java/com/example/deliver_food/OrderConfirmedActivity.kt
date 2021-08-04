package com.example.deliver_food

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmedActivity : AppCompatActivity() {
    lateinit var btnOk: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmed)

        btnOk = findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent = Intent(this@OrderConfirmedActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@OrderConfirmedActivity, MainActivity::class.java)
        startActivity(intent)


    }
}