package com.example.appzetar.splash

import kotlin.time.Duration.Companion.milliseconds
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appzetar.R
import com.example.appzetar.usuario.ActivityMenuUsuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.annotation.SuppressLint

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val ivDelivery = findViewById<ImageView>(R.id.ivDelivery)

        lifecycleScope.launch {

            for (progress in 1..100) {
                delay(20.milliseconds)

                progressBar.progress = progress
                tvStatus.text = getString(
                    R.string.splash_preparando_experiencia,
                    progress
                )

                ivDelivery.translationX =
                    progress * 3f
            }

            val intent = Intent(
                this@SplashActivity,
                ActivityMenuUsuario::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            val transitionOptions =
                ActivityOptionsCompat.makeCustomAnimation(
                    this@SplashActivity,
                    0,
                    0
                )

            startActivity(
                intent,
                transitionOptions.toBundle()
            )

            finish()
        }
    }
}
