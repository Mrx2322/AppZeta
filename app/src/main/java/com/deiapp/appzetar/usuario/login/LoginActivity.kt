package com.deiapp.appzetar.usuario.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.deiapp.appzeta.R
import com.deiapp.appzetar.splash.SplashActivity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "LoginActivity"
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText

    private lateinit var btnIniciarSesion: Button
    private lateinit var btnGoogle: MaterialButton
    private lateinit var btnInvitado: MaterialButton

    private lateinit var tvRegistrarse: TextView

    // =========================================================
    // FIREBASE / CREDENTIAL MANAGER
    // =========================================================

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    private val db =
        FirebaseFirestore.getInstance()

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

        setContentView(
            R.layout.activity_login
        )

        configurarInsets()

        auth =
            FirebaseAuth.getInstance()

        credentialManager =
            CredentialManager.create(this)

        inicializarVistas()
        configurarEventos()
    }

    // =========================================================
    // INSETS
    // =========================================================

    private fun configurarInsets() {

        val root =
            findViewById<View>(
                R.id.main
            )

        val paddingInicialIzquierdo =
            root.paddingLeft

        val paddingInicialSuperior =
            root.paddingTop

        val paddingInicialDerecho =
            root.paddingRight

        val paddingInicialInferior =
            root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(
            root
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
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

        etCorreo =
            findViewById(R.id.etCorreo)

        etContrasena =
            findViewById(R.id.etContrasena)

        btnIniciarSesion =
            findViewById(R.id.btnIniciarSesion)

        btnGoogle =
            findViewById(R.id.btnGoogle)

        btnInvitado =
            findViewById(R.id.btnInvitado)

        tvRegistrarse =
            findViewById(R.id.tvRegistrarse)
    }

    // =========================================================
    // EVENTOS
    // =========================================================

    private fun configurarEventos() {

        btnIniciarSesion.setOnClickListener {
            iniciarSesion()
        }

        btnGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }

        btnInvitado.setOnClickListener {
            continuarComoInvitado()
        }

        tvRegistrarse.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegistroActivity::class.java
                )
            )
        }
    }

    // =========================================================
    // LOGIN CORREO Y CONTRASEÑA
    // =========================================================

    private fun iniciarSesion() {

        val correo =
            etCorreo.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val contrasena =
            etContrasena.text
                ?.toString()
                ?.trim()
                .orEmpty()

        if (correo.isEmpty()) {

            etCorreo.error =
                getString(
                    R.string.login_error_ingresa_correo
                )

            return
        }

        if (contrasena.isEmpty()) {

            etContrasena.error =
                getString(
                    R.string.login_error_ingresa_contrasena
                )

            return
        }

        btnIniciarSesion.isEnabled =
            false

        auth.signInWithEmailAndPassword(
            correo,
            contrasena
        )
            .addOnSuccessListener {

                abrirMenuUsuario(
                    getString(
                        R.string.login_inicio_correcto
                    )
                )
            }
            .addOnFailureListener { error ->

                btnIniciarSesion.isEnabled =
                    true

                Log.e(
                    TAG,
                    "Error al iniciar sesión con correo",
                    error
                )

                Toast.makeText(
                    this,
                    getString(
                        R.string.login_error_credenciales
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // INVITADO
    // =========================================================

    private fun continuarComoInvitado() {

        btnInvitado.isEnabled =
            false

        val usuarioActual =
            auth.currentUser

        if (usuarioActual?.isAnonymous == true) {

            crearPerfilInvitadoSiNoExiste()

            return
        }

        auth.signInAnonymously()
            .addOnSuccessListener {

                crearPerfilInvitadoSiNoExiste()
            }
            .addOnFailureListener { error ->

                btnInvitado.isEnabled =
                    true

                Log.e(
                    TAG,
                    "Error al iniciar como invitado",
                    error
                )

                Toast.makeText(
                    this,
                    getString(
                        R.string.login_error_ingreso_invitado
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // PERFIL INVITADO
    // =========================================================

    private fun crearPerfilInvitadoSiNoExiste() {

        val usuario =
            auth.currentUser
                ?: run {

                    btnInvitado.isEnabled =
                        true

                    return
                }

        val referencia =
            db.collection("usuarios")
                .document(usuario.uid)

        referencia.get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    abrirMenuUsuario(
                        getString(
                            R.string.login_bienvenida_zeta
                        )
                    )

                    return@addOnSuccessListener
                }

                val datos =
                    hashMapOf(
                        "uid" to usuario.uid,
                        "nombre" to getString(
                            R.string.login_nombre_invitado
                        ),
                        "correo" to "",
                        "esInvitado" to true
                    )

                referencia.set(datos)
                    .addOnSuccessListener {

                        abrirMenuUsuario(
                            getString(
                                R.string.login_bienvenida_zeta
                            )
                        )
                    }
                    .addOnFailureListener { error ->

                        btnInvitado.isEnabled =
                            true

                        Log.e(
                            TAG,
                            "Error creando perfil invitado",
                            error
                        )

                        Toast.makeText(
                            this,
                            getString(
                                R.string.login_error_preparar_perfil_invitado
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { error ->

                btnInvitado.isEnabled =
                    true

                Log.e(
                    TAG,
                    "Error verificando perfil invitado",
                    error
                )

                Toast.makeText(
                    this,
                    getString(
                        R.string.login_error_verificar_perfil_invitado
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // GOOGLE SIGN-IN
    // =========================================================

    private fun iniciarSesionConGoogle() {

        btnGoogle.isEnabled =
            false

        /*
         * Para un botón explícito "Continuar con Google"
         * utilizamos GetSignInWithGoogleOption.
         */
        val googleOption =
            GetSignInWithGoogleOption.Builder(
                getString(
                    R.string.default_web_client_id
                )
            )
                .build()

        val solicitud =
            GetCredentialRequest.Builder()
                .addCredentialOption(
                    googleOption
                )
                .build()

        credentialManager.getCredentialAsync(
            this,
            solicitud,
            null,
            ContextCompat.getMainExecutor(this),
            object :
                CredentialManagerCallback<
                        GetCredentialResponse,
                        GetCredentialException
                        > {

                override fun onResult(
                    result: GetCredentialResponse
                ) {

                    procesarCredencialGoogle(
                        result.credential
                    )
                }

                override fun onError(
                    e: GetCredentialException
                ) {

                    btnGoogle.isEnabled =
                        true

                    Log.e(
                        TAG,
                        "Google Credential Manager error: " +
                                "${e.javaClass.simpleName} - ${e.message}",
                        e
                    )

                    Toast.makeText(
                        this@LoginActivity,
                        getString(
                            R.string.login_error_google
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // =========================================================
    // PROCESAR CREDENCIAL GOOGLE
    // =========================================================

    private fun procesarCredencialGoogle(
        credential: Credential
    ) {

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(
                        credential.data
                    )

                autenticarConFirebase(
                    googleCredential.idToken
                )

            } catch (error: Exception) {

                btnGoogle.isEnabled =
                    true

                Log.e(
                    TAG,
                    "Error procesando credencial Google",
                    error
                )

                Toast.makeText(
                    this,
                    getString(
                        R.string.login_error_obtener_cuenta_google
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            btnGoogle.isEnabled =
                true

            Log.e(
                TAG,
                "Tipo de credencial Google no reconocido: ${credential.type}"
            )

            Toast.makeText(
                this,
                getString(
                    R.string.login_error_obtener_cuenta_google
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // FIREBASE GOOGLE
    // =========================================================

    private fun autenticarConFirebase(
        idToken: String
    ) {

        val firebaseCredential =
            GoogleAuthProvider.getCredential(
                idToken,
                null
            )

        auth.signInWithCredential(
            firebaseCredential
        )
            .addOnSuccessListener {

                crearPerfilGoogleSiNoExiste()
            }
            .addOnFailureListener { error ->

                btnGoogle.isEnabled =
                    true

                Log.e(
                    TAG,
                    "ERROR FIREBASE GOOGLE: " +
                            "${error.javaClass.simpleName} - ${error.message}",
                    error
                )

                Toast.makeText(
                    this,
                    getString(
                        R.string.login_error_google
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // PERFIL GOOGLE
    // =========================================================

    private fun crearPerfilGoogleSiNoExiste() {

        val usuario =
            auth.currentUser
                ?: run {

                    btnGoogle.isEnabled =
                        true

                    return
                }

        val referencia =
            db.collection("usuarios")
                .document(usuario.uid)

        referencia.get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    abrirMenuUsuario(
                        getString(
                            R.string.login_inicio_correcto
                        )
                    )

                    return@addOnSuccessListener
                }

                val datos =
                    hashMapOf(
                        "uid" to usuario.uid,

                        "nombre" to (
                                usuario.displayName
                                    ?: getString(
                                        R.string.login_nombre_cliente
                                    )
                                ),

                        "correo" to (
                                usuario.email
                                    ?: ""
                                ),

                        /*
                         * Importante para que coincida con
                         * las reglas actuales de usuarios.
                         */
                        "esInvitado" to false
                    )

                referencia.set(datos)
                    .addOnSuccessListener {

                        abrirMenuUsuario(
                            getString(
                                R.string.login_inicio_correcto
                            )
                        )
                    }
                    .addOnFailureListener { error ->

                        btnGoogle.isEnabled =
                            true

                        Log.e(
                            TAG,
                            "Error creando perfil Google",
                            error
                        )

                        Toast.makeText(
                            this,
                            getString(
                                R.string.login_error_crear_perfil
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { error ->

                btnGoogle.isEnabled =
                    true

                Log.e(
                    TAG,
                    "Error verificando perfil Google",
                    error
                )

                Toast.makeText(
                    this,
                    getString(
                        R.string.login_error_verificar_perfil
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // =========================================================
    // ABRIR APP
    // =========================================================

    private fun abrirMenuUsuario(
        mensaje: String
    ) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()

        val intent =
            Intent(
                this,
                SplashActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        startActivity(intent)

        finish()
    }
}