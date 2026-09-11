package com.example.appzetar.splash

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appzetar.R
import com.example.appzetar.Usuario.ActivityMenuUsuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Muestra la imagen HD + Barra de progreso

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val ivDelivery = findViewById<ImageView>(R.id.ivDelivery) // 1. Referencia a la imagen

        lifecycleScope.launch {
            for (progress in 1..100) {
                delay(20)
                progressBar.progress = progress
                tvStatus.text = "Preparando tu experiencia... ($progress%)"

                // Desplaza la moto hacia la derecha conforme aumenta el porcentaje
                ivDelivery.translationX = (progress * 3).toFloat()
            }

            val intent = Intent(
                this@SplashActivity,
                ActivityMenuUsuario::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}
