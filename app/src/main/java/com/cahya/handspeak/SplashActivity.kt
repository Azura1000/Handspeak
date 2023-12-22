package com.cahya.handspeak

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashImageView: ImageView = findViewById(R.id.splashImageView)

        // Load fade-in animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashImageView.startAnimation(fadeIn)

        // Delay for 3 seconds and then open MainActivity
        Handler().postDelayed({
            navigateToMainActivity()
        }, SPLASH_DELAY)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
