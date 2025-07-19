package com.example.practice4

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.View
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

// MainActivity
// Hosts the NavigationDrawer with different fragments (Home, Items, Account).
// Displays logged-in user’s name in the drawer header.
// Handles logout.
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout  // The main DrawerLayout
    private lateinit var navigationView: NavigationView  // The NavigationView inside the drawer
    private lateinit var auth: FirebaseAuth   // Firebase authentication instance

    // Called when the activity is created.
    // Sets up the UI, navigation drawer, and default fragment.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Set up ActionBarDrawerToggle for opening/closing the drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set listener for navigation item selection
        navigationView.setNavigationItemSelectedListener(this)

        // Get logged-in user's name from intent and display in header
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.textViewUserName)
        userNameTextView.text = "Hello $userName"

        // Load default fragment (HomeFragment) if first time
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    // Called when an item in the app bar is selected.
    // Handles opening/closing of the navigation drawer toggle.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Called when a navigation item is selected from the drawer.
    // Loads the corresponding fragment or performs logout.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_items -> {
                // Show ItemListFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ItemListFragment())
                    .commit()
            }
            R.id.nav_account -> {
                // Show AccountFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment())
                    .commit()
            }
            R.id.nav_logout -> {
                // Sign out and return to LoginActivity
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            else -> {
                // Default to HomeFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        }

        // Close the navigation drawer after selection
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Called when the back button is pressed.
    // Closes the drawer if open; otherwise behaves normally.
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
