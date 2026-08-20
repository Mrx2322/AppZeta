package com.example.appzetar.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appzetar.Menu.ActivityMenu
import com.example.appzetar.R
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
                delay(30)
                progressBar.progress = progress
                tvStatus.text = "Cargando menú... ($progress%)"

                // Desplaza la moto hacia la derecha conforme aumenta el porcentaje
                ivDelivery.translationX = (progress * 3).toFloat()
            }

            // Abre tu siguiente layout/pantalla principal
            val intent = Intent(this@SplashActivity, ActivityMenu::class.java)
            startActivity(intent)
            finish() // Cierra la pantalla de carga para no regresar a ella con el botón 'atrás'
        }
    }
}