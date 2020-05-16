package com.internshala.FoodRunnerApp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.FoodRunnerApp.R
import com.internshala.FoodRunnerApp.fragment.*

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences=getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)

        drawerLayout=findViewById(R.id.drawerLayout)
        navigationView=findViewById(R.id.navigationView)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)

        setUpToolbar()
        opendashboard()

        val actionBarDrawerToggle= ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.closed_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null)
                previousMenuItem?.isChecked=false
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId)
            {
                R.id.home ->{
                    opendashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavouritesFragment()
                        )
                        .commit()
                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.order ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title="Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faq ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FAQFragment()
                        )
                        .commit()
                    supportActionBar?.title ="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logout->{
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to logout?")
                    dialog.setPositiveButton("YES") { _, _ ->
                        sharedPreferences.edit().putBoolean("isLogged",false).apply()
                        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                        finish()
                    }
                    dialog.setNegativeButton("NO") { _, _ ->
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun opendashboard()
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .addToBackStack("Home")
            .commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag){
            !is HomeFragment ->opendashboard()
            else->{
                finish()
            }
        }
    }
}
