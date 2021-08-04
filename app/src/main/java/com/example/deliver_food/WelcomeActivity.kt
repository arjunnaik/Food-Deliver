package com.example.deliver_food

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)


        Handler().postDelayed({
            val startAct = Intent(this@WelcomeActivity, LogInActivity::class.java)
            startActivity(startAct)
        }, 1000)

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}