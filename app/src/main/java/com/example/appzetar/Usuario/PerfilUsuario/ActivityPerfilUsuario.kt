package com.example.appzetar.Usuario

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityPerfilUsuario : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // =========================================================
    // UI
    // =========================================================

    private lateinit var tvInicialPerfil: TextView
    private lateinit var tvNombrePerfil: TextView
    private lateinit var tvCorreoPerfil: TextView

    private lateinit var opcionEditarPerfil: LinearLayout
    private lateinit var opcionPedidosPerfil: LinearLayout

    private lateinit var btnCerrarSesion: MaterialButton

    // =========================================================
    // DATOS
    // =========================================================

    private var nombreActual = ""
    private var correoActual = ""
    private var animacionInicialEjecutada = false

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

        setContentView(R.layout.activity_perfil_usuario)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initComponent()
        initUI()
        prepararAnimaciones()
        cargarDatosUsuario()
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun initComponent() {

        tvInicialPerfil = findViewById(R.id.tvInicialPerfil)
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil)
        tvCorreoPerfil = findViewById(R.id.tvCorreoPerfil)

        opcionEditarPerfil = findViewById(R.id.opcionEditarPerfil)
        opcionPedidosPerfil = findViewById(R.id.opcionPedidosPerfil)

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
    }

    // =========================================================
    // UI
    // =========================================================

    private fun initUI() {

        opcionEditarPerfil.setOnClickListener {
            mostrarDialogEditarNombre()
        }

        opcionPedidosPerfil.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ActivityPedidosUsuario::class.java
                )
            )
        }

        btnCerrarSesion.setOnClickListener {
            mostrarConfirmacionCerrarSesion()
        }
    }

    // =========================================================
    // PREPARAR ANIMACIONES
    // =========================================================

    private fun prepararAnimaciones() {

        tvInicialPerfil.alpha = 0f
        tvInicialPerfil.scaleX = 0.85f
        tvInicialPerfil.scaleY = 0.85f

        tvNombrePerfil.alpha = 0f
        tvNombrePerfil.translationY = 18f

        tvCorreoPerfil.alpha = 0f
        tvCorreoPerfil.translationY = 18f

        opcionEditarPerfil.alpha = 0f
        opcionEditarPerfil.translationY = 25f

        opcionPedidosPerfil.alpha = 0f
        opcionPedidosPerfil.translationY = 25f

        btnCerrarSesion.alpha = 0f
        btnCerrarSesion.translationY = 25f
    }

    // =========================================================
    // ANIMACIÓN DE ENTRADA
    // =========================================================

    private fun ejecutarAnimacionInicial() {

        if (animacionInicialEjecutada) return

        animacionInicialEjecutada = true

        // Avatar: aparición y escala suave.
        AnimatorSet().apply {

            playTogether(
                ObjectAnimator.ofFloat(
                    tvInicialPerfil,
                    View.ALPHA,
                    0f,
                    1f
                ),
                ObjectAnimator.ofFloat(
                    tvInicialPerfil,
                    View.SCALE_X,
                    0.85f,
                    1f
                ),
                ObjectAnimator.ofFloat(
                    tvInicialPerfil,
                    View.SCALE_Y,
                    0.85f,
                    1f
                )
            )

            duration = 450
            startDelay = 80
            interpolator = DecelerateInterpolator()

            start()
        }

        // Nombre y correo.
        animarEntrada(tvNombrePerfil, 160)
        animarEntrada(tvCorreoPerfil, 230)

        // Opciones.
        animarEntrada(opcionEditarPerfil, 320)
        animarEntrada(opcionPedidosPerfil, 400)

        // Cerrar sesión.
        animarEntrada(btnCerrarSesion, 480)
    }

    // =========================================================
    // ANIMACIÓN REUTILIZABLE
    // =========================================================

    private fun animarEntrada(
        vista: View,
        retraso: Long
    ) {

        AnimatorSet().apply {

            playTogether(
                ObjectAnimator.ofFloat(
                    vista,
                    View.ALPHA,
                    0f,
                    1f
                ),
                ObjectAnimator.ofFloat(
                    vista,
                    View.TRANSLATION_Y,
                    vista.translationY,
                    0f
                )
            )

            duration = 420
            startDelay = retraso
            interpolator = DecelerateInterpolator()

            start()
        }
    }

    // =========================================================
    // CARGAR DATOS DEL USUARIO
    // =========================================================

    private fun cargarDatosUsuario() {

        val usuario = auth.currentUser

        if (usuario == null) {
            regresarLogin()
            return
        }

        correoActual = usuario.email ?: ""

        db.collection("usuarios")
            .document(usuario.uid)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    nombreActual =
                        documento.getString("nombre")
                            ?: "Usuario"

                    correoActual =
                        documento.getString("correo")
                            ?: usuario.email
                                    ?: ""

                } else {

                    nombreActual =
                        usuario.displayName
                            ?: "Usuario"

                    correoActual =
                        usuario.email
                            ?: ""
                }

                mostrarDatos()
                ejecutarAnimacionInicial()
            }
            .addOnFailureListener {

                nombreActual =
                    usuario.displayName
                        ?: "Usuario"

                correoActual =
                    usuario.email
                        ?: ""

                mostrarDatos()
                ejecutarAnimacionInicial()

                Toast.makeText(
                    this,
                    "No se pudieron cargar todos los datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // MOSTRAR DATOS
    // =========================================================

    private fun mostrarDatos() {

        tvNombrePerfil.text = nombreActual
        tvCorreoPerfil.text = correoActual

        val inicial =
            nombreActual
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString()
                ?: "U"

        tvInicialPerfil.text = inicial
    }

    // =========================================================
    // EDITAR NOMBRE
    // =========================================================

    private fun mostrarDialogEditarNombre() {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_editar_perfil,
                null
            )

        val etNombreEditar =
            dialogView.findViewById<TextInputEditText>(
                R.id.etNombreEditar
            )

        val btnCancelar =
            dialogView.findViewById<MaterialButton>(
                R.id.btnCancelarEditarPerfil
            )

        val btnGuardar =
            dialogView.findViewById<MaterialButton>(
                R.id.btnGuardarEditarPerfil
            )

        etNombreEditar.setText(nombreActual)

        val dialog =
            AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialog.show()

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnGuardar.setOnClickListener {

            val nuevoNombre =
                etNombreEditar.text
                    ?.toString()
                    ?.trim()
                    ?: ""

            if (nuevoNombre.isEmpty()) {

                etNombreEditar.error =
                    "Ingresa tu nombre"

                return@setOnClickListener
            }

            actualizarNombre(
                nuevoNombre,
                dialog,
                btnGuardar
            )
        }
    }

    // =========================================================
    // ACTUALIZAR NOMBRE
    // =========================================================

    private fun actualizarNombre(
        nuevoNombre: String,
        dialog: AlertDialog,
        btnGuardar: MaterialButton
    ) {

        val usuario = auth.currentUser

        if (usuario == null) {
            regresarLogin()
            return
        }

        btnGuardar.isEnabled = false

        db.collection("usuarios")
            .document(usuario.uid)
            .update("nombre", nuevoNombre)
            .addOnSuccessListener {

                nombreActual = nuevoNombre
                mostrarDatos()

                Toast.makeText(
                    this,
                    "Perfil actualizado",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
            .addOnFailureListener {

                btnGuardar.isEnabled = true

                Toast.makeText(
                    this,
                    "No se pudo actualizar el perfil",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // CONFIRMAR CIERRE DE SESIÓN
    // =========================================================

    private fun mostrarConfirmacionCerrarSesion() {

        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar tu sesión?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Cerrar sesión") { _, _ ->
                cerrarSesion()
            }
            .show()
    }

    // =========================================================
    // CERRAR SESIÓN
    // =========================================================

    private fun cerrarSesion() {

        auth.signOut()
        regresarLogin()
    }

    // =========================================================
    // REGRESAR AL LOGIN
    // =========================================================

    private fun regresarLogin() {

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}