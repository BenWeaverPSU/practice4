// SplashActivity.kt
package com.example.practice4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// SplashActivity
// Shows a splash screen for a few seconds, then navigates to LoginActivity.

class SplashActivity : AppCompatActivity() {

    //  Called when the activity is created.
    //  Sets splash screen layout and schedules navigation.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Post a delayed task (5 seconds) to move to next screen
        Handler(Looper.getMainLooper()).postDelayed({
            // If a user is already logged in, navigate to MainActivity
            val intent = if (FirebaseAuth.getInstance().currentUser != null) {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_NAME", FirebaseAuth.getInstance().currentUser?.email ?: "User")
                }
                // Navigate to login activity if user is already logged in
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 5000)
    }
}

