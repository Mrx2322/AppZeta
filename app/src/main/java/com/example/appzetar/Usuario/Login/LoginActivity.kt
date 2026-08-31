package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appzetar.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText

    private lateinit var btnIniciarSesion: Button
    private lateinit var tvRegistrarse: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_login
        )

        auth = FirebaseAuth.getInstance()

        etCorreo =
            findViewById(R.id.etCorreo)

        etContrasena =
            findViewById(R.id.etContrasena)

        btnIniciarSesion =
            findViewById(R.id.btnIniciarSesion)

        tvRegistrarse =
            findViewById(R.id.tvRegistrarse)


        // =====================================================
        // INICIAR SESIÓN
        // =====================================================

        btnIniciarSesion.setOnClickListener {

            iniciarSesion()
        }


        // =====================================================
        // REGISTRARSE
        // =====================================================

        tvRegistrarse.setOnClickListener {

            val intent =
                Intent(
                    this,
                    RegistroActivity::class.java
                )

            startActivity(intent)
        }
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private fun iniciarSesion() {

        val correo =
            etCorreo.text
                ?.toString()
                ?.trim()
                ?: ""

        val contrasena =
            etContrasena.text
                ?.toString()
                ?.trim()
                ?: ""


        // -----------------------------------------------------
        // VALIDAR CORREO
        // -----------------------------------------------------

        if (correo.isEmpty()) {

            etCorreo.error =
                "Ingresa tu correo"

            return
        }


        // -----------------------------------------------------
        // VALIDAR CONTRASEÑA
        // -----------------------------------------------------

        if (contrasena.isEmpty()) {

            etContrasena.error =
                "Ingresa tu contraseña"

            return
        }


        // -----------------------------------------------------
        // FIREBASE AUTH
        // -----------------------------------------------------

        btnIniciarSesion.isEnabled = false


        auth.signInWithEmailAndPassword(
            correo,
            contrasena
        )
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Inicio de sesión correcto",
                    Toast.LENGTH_SHORT
                ).show()


                val intent =
                    Intent(
                        this,
                        ActivityMenuUsuario::class.java
                    )


                // Evita regresar al login con el botón atrás
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK


                startActivity(intent)

                finish()
            }


            .addOnFailureListener { error ->

                btnIniciarSesion.isEnabled = true


                Toast.makeText(
                    this,
                    "Correo o contraseña incorrectos",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}