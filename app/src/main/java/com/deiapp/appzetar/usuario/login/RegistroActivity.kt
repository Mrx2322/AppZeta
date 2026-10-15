package com.deiapp.appzetar.usuario.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etConfirmarContrasena: TextInputEditText

    private lateinit var btnRegistrarse: Button
    private lateinit var cbAceptaPrivacidad: CheckBox
    private lateinit var tvPoliticaPrivacidad: View

    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(R.layout.activity_registro)

        configurarInsets()

        auth = FirebaseAuth.getInstance()

        inicializarVistas()

        btnRegistrarse.setOnClickListener {
            registrarUsuario()
        }

        tvPoliticaPrivacidad.setOnClickListener {
            abrirPoliticaPrivacidad()
        }
    }

    // =========================================================
    // INSETS
    // =========================================================

    private fun configurarInsets() {
        val root = findViewById<View>(R.id.main)

        val paddingInicialIzquierdo = root.paddingLeft
        val paddingInicialSuperior = root.paddingTop
        val paddingInicialDerecho = root.paddingRight
        val paddingInicialInferior = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(
            root
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                paddingInicialIzquierdo + systemBars.left,
                paddingInicialSuperior + systemBars.top,
                paddingInicialDerecho + systemBars.right,
                paddingInicialInferior + systemBars.bottom
            )

            insets
        }
    }

    // =========================================================
    // INICIALIZAR VISTAS
    // =========================================================

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etConfirmarContrasena =
            findViewById(R.id.etConfirmarContrasena)

        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        cbAceptaPrivacidad = findViewById(R.id.cbAceptaPrivacidad)
        tvPoliticaPrivacidad = findViewById(R.id.tvPoliticaPrivacidad)
    }

    // =========================================================
    // REGISTRAR USUARIO
    // =========================================================

    private fun registrarUsuario() {
        val nombre = etNombre.text
            ?.toString()
            ?.trim()
            ?: ""

        val correo = etCorreo.text
            ?.toString()
            ?.trim()
            ?: ""

        val contrasena = etContrasena.text
            ?.toString()
            ?.trim()
            ?: ""

        val confirmar = etConfirmarContrasena.text
            ?.toString()
            ?.trim()
            ?: ""

        // =====================================================
        // VALIDACIONES
        // =====================================================

        if (nombre.isEmpty()) {
            etNombre.error =
                getString(R.string.registro_error_ingresa_nombre)

            return
        }

        if (correo.isEmpty()) {
            etCorreo.error =
                getString(R.string.registro_error_ingresa_correo)

            return
        }

        if (contrasena.isEmpty()) {
            etContrasena.error =
                getString(R.string.registro_error_ingresa_contrasena)

            return
        }

        if (contrasena.length < 6) {
            etContrasena.error =
                getString(R.string.registro_error_minimo_caracteres)

            return
        }

        if (confirmar.isEmpty()) {
            etConfirmarContrasena.error =
                getString(R.string.registro_error_confirma_contrasena)

            return
        }

        if (contrasena != confirmar) {
            etConfirmarContrasena.error =
                getString(R.string.registro_error_contrasenas_no_coinciden)

            return
        }

        if (!cbAceptaPrivacidad.isChecked) {
            Toast.makeText(
                this,
                R.string.privacidad_debes_aceptar,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // =====================================================
        // DESACTIVAR BOTÓN
        // =====================================================

        btnRegistrarse.isEnabled = false

        // =====================================================
        // CREAR USUARIO EN FIREBASE AUTH
        // =====================================================

        auth.createUserWithEmailAndPassword(
            correo,
            contrasena
        )
            .addOnSuccessListener {
                val usuario = auth.currentUser

                if (usuario == null) {
                    btnRegistrarse.isEnabled = true
                    return@addOnSuccessListener
                }

                val uid = usuario.uid

                // =============================================
                // DATOS DEL USUARIO
                // =============================================

                val datos = hashMapOf(
                    "uid" to uid,
                    "nombre" to nombre,
                    "correo" to correo,
                    "privacidadVersion" to VERSION_POLITICA_PRIVACIDAD,
                    "privacidadAceptadaEn" to FieldValue.serverTimestamp()
                )

                // =============================================
                // GUARDAR PERFIL EN FIRESTORE
                // =============================================

                db.collection("usuarios")
                    .document(uid)
                    .set(datos)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            getString(
                                R.string.registro_cuenta_creada_exito
                            ),
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this,
                            ActivityMenuUsuario::class.java
                        )

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        btnRegistrarse.isEnabled = true

                        Toast.makeText(
                            this,
                            getString(
                                R.string.registro_error_guardar_datos
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { error ->
                btnRegistrarse.isEnabled = true

                Toast.makeText(
                    this,
                    error.message
                        ?: getString(
                            R.string.registro_error_crear_cuenta
                        ),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun abrirPoliticaPrivacidad() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(URL_POLITICA_PRIVACIDAD)
            )
        )
    }

    private companion object {
        const val URL_POLITICA_PRIVACIDAD = "https://appzeta-f919f.web.app/privacidad"
        const val VERSION_POLITICA_PRIVACIDAD = "2026-10-15"
    }
}
