// SplashActivity.kt
package com.example.practice4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * SplashActivity
 * Shows a splash screen for a few seconds, then navigates to LoginActivity.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (FirebaseAuth.getInstance().currentUser != null) {
                Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_NAME", FirebaseAuth.getInstance().currentUser?.email ?: "User")
                }
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}

