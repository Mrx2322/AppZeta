package com.example.appzetar.usuario.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.appzetar.R
import com.example.appzetar.splash.SplashActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
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

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnGoogle: MaterialButton
    private lateinit var btnInvitado: MaterialButton
    private lateinit var tvRegistrarse: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnInvitado = findViewById(R.id.btnInvitado)
        tvRegistrarse = findViewById(R.id.tvRegistrarse)

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

    private fun iniciarSesion() {

        val correo = etCorreo.text?.toString()?.trim().orEmpty()
        val contrasena = etContrasena.text?.toString()?.trim().orEmpty()

        if (correo.isEmpty()) {
            etCorreo.error = getString(R.string.login_error_ingresa_correo)
            return
        }

        if (contrasena.isEmpty()) {
            etContrasena.error = getString(R.string.login_error_ingresa_contrasena)
            return
        }

        btnIniciarSesion.isEnabled = false

        auth.signInWithEmailAndPassword(
            correo,
            contrasena
        )
            .addOnSuccessListener {
                abrirMenuUsuario(getString(R.string.login_inicio_correcto))
            }
            .addOnFailureListener {
                btnIniciarSesion.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.login_error_credenciales),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun continuarComoInvitado() {

        btnInvitado.isEnabled = false

        val usuarioActual = auth.currentUser

        if (usuarioActual?.isAnonymous == true) {
            crearPerfilInvitadoSiNoExiste()
            return
        }

        auth.signInAnonymously()
            .addOnSuccessListener {
                crearPerfilInvitadoSiNoExiste()
            }
            .addOnFailureListener {
                btnInvitado.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.login_error_ingreso_invitado),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun crearPerfilInvitadoSiNoExiste() {

        val usuario = auth.currentUser ?: run {
            btnInvitado.isEnabled = true
            return
        }

        val referencia = db.collection("usuarios")
            .document(usuario.uid)

        referencia.get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {
                    abrirMenuUsuario(getString(R.string.login_bienvenida_zeta))
                    return@addOnSuccessListener
                }

                val datos = hashMapOf(
                    "uid" to usuario.uid,
                    "nombre" to getString(R.string.login_nombre_invitado),
                    "correo" to "",
                    "esInvitado" to true
                )

                referencia.set(datos)
                    .addOnSuccessListener {
                        abrirMenuUsuario(getString(R.string.login_bienvenida_zeta))
                    }
                    .addOnFailureListener {
                        btnInvitado.isEnabled = true

                        Toast.makeText(
                            this,
                            getString(R.string.login_error_preparar_perfil_invitado),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                btnInvitado.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.login_error_verificar_perfil_invitado),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun iniciarSesionConGoogle() {

        btnGoogle.isEnabled = false

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(
                getString(R.string.default_web_client_id)
            )
            .build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        credentialManager.getCredentialAsync(
            this,
            solicitud,
            null,
            ContextCompat.getMainExecutor(this),
            object : CredentialManagerCallback<
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
                    btnGoogle.isEnabled = true
                    Log.w(TAG, "Google Credential Manager error", e)

                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_error_google),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun procesarCredencialGoogle(
        credential: Credential
    ) {

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential =
                GoogleIdTokenCredential.createFrom(
                    credential.data
                )

            autenticarConFirebase(
                googleCredential.idToken
            )
        } else {
            btnGoogle.isEnabled = true

            Toast.makeText(
                this,
                getString(R.string.login_error_obtener_cuenta_google),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun autenticarConFirebase(
        idToken: String
    ) {

        val credential = GoogleAuthProvider.getCredential(
            idToken,
            null
        )

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                crearPerfilGoogleSiNoExiste()
            }
            .addOnFailureListener {
                btnGoogle.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.login_error_google),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun crearPerfilGoogleSiNoExiste() {

        val usuario = auth.currentUser ?: return

        val referencia = db.collection("usuarios")
            .document(usuario.uid)

        referencia.get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {
                    abrirMenuUsuario(getString(R.string.login_inicio_correcto))
                    return@addOnSuccessListener
                }

                val datos = hashMapOf(
                    "uid" to usuario.uid,
                    "nombre" to (usuario.displayName
                        ?: getString(R.string.login_nombre_cliente)),
                    "correo" to (usuario.email ?: "")
                )

                referencia.set(datos)
                    .addOnSuccessListener {
                        abrirMenuUsuario(getString(R.string.login_inicio_correcto))
                    }
                    .addOnFailureListener {
                        btnGoogle.isEnabled = true

                        Toast.makeText(
                            this,
                            getString(R.string.login_error_crear_perfil),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {
                btnGoogle.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.login_error_verificar_perfil),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun abrirMenuUsuario(
        mensaje: String
    ) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(
            this,
            SplashActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}
