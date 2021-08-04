package com.example.deliver_food

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.deliver_food.fragments.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var txtUserName: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)

        val drawerHeader = navigationView.getHeaderView(0)
        txtMobileNumber = drawerHeader.findViewById(R.id.txtMobileNumber)
        txtUserName = drawerHeader.findViewById(R.id.txtUserName)

        txtUserName.text = sharedPreferences.getString("name", "Username")
        txtMobileNumber.text = sharedPreferences.getString("mobile_number", "+91 9876543210")

        setUpToolBar()
        openAllRestaurantsFragment()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it


            when (it.itemId) {
                R.id.itemHome -> {
                    openAllRestaurantsFragment()
                    drawerLayout.closeDrawers()
                }

                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        MyProfileFragment()
                    ).commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FavouritesFragment()
                    ).commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrderHistory())
                        .commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()

                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FAQsFragment())
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()

                }
                R.id.logOut -> {

                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you want to log out?")

                    dialog.setPositiveButton("NO") { text, listner ->
                        drawerLayout.closeDrawers()

                    }
                    dialog.setNegativeButton("YES") { text, listner ->
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        var intent = Intent(this@MainActivity, LogInActivity::class.java)
                        startActivity(intent)
                    }

                    dialog.create()
                    dialog.show()


                }
            }
            return@setNavigationItemSelectedListener true
        }

    }


    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    fun openAllRestaurantsFragment() {
        val fragment = AllRestaurantsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        navigationView.setCheckedItem(R.id.itemHome)
        supportActionBar?.title = "All Restaurants"
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is AllRestaurantsFragment -> openAllRestaurantsFragment()
            else -> {
                super.onBackPressed()
                finishAffinity()
            }
        }


    }


}








