package com.example.appzetar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appzetar.Usuario.LoginActivity
import com.example.appzetar.splash.SplashActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // =====================================================
        // BOTÓN VISTA ADMIN
        // =====================================================

        val btnInicio =
            findViewById<Button>(R.id.btnIngresar)

        btnInicio.setOnClickListener {
            navigateToIngresar()
        }

        // =====================================================
        // BOTÓN VISTA USUARIO
        // =====================================================

        val btnUsuario =
            findViewById<Button>(R.id.btnUsuario)

        btnUsuario.setOnClickListener {
            navigateToLogin()
        }
    }

    // =========================================================
    // IR A ADMIN
    // =========================================================

    private fun navigateToIngresar() {

        val intent =
            Intent(
                this,
                SplashActivity::class.java
            )

        startActivity(intent)
    }

    // =========================================================
    // IR AL LOGIN DE USUARIO
    // =========================================================

    private fun navigateToLogin() {

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )

        startActivity(intent)
    }
}