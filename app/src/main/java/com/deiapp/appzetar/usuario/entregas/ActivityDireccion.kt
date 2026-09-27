package com.deiapp.appzetar.usuario.entregas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.pedidos.ActivityConfirmarPedido
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ActivityDireccion : AppCompatActivity() {

    private lateinit var etDireccion: TextInputEditText
    private lateinit var etReferencia: TextInputEditText
    private lateinit var etTelefono: TextInputEditText

    private lateinit var btnUsarUbicacion: MaterialButton
    private lateinit var tvEstadoUbicacion: TextView
    private lateinit var btnContinuar: MaterialButton

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var latitud: Double? = null
    private var longitud: Double? = null
    private var ubicacionMaps: String? = null

    private val solicitarPermisosUbicacion =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->

            val ubicacionPrecisa =
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val ubicacionAproximada =
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (ubicacionPrecisa || ubicacionAproximada) {
                obtenerUbicacionActual()
            } else {
                mostrarUbicacionNoAutorizada()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_direccion)

        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas() {

        etDireccion =
            findViewById(R.id.etDireccion)

        etReferencia =
            findViewById(R.id.etReferencia)

        etTelefono =
            findViewById(R.id.etTelefono)

        btnUsarUbicacion =
            findViewById(R.id.btnUsarUbicacion)

        tvEstadoUbicacion =
            findViewById(R.id.tvEstadoUbicacion)

        btnContinuar =
            findViewById(R.id.btnContinuarDireccion)
    }

    private fun configurarEventos() {

        btnUsarUbicacion.setOnClickListener {
            comprobarPermisosUbicacion()
        }

        btnContinuar.setOnClickListener {
            continuarAConfirmacion()
        }
    }

    private fun comprobarPermisosUbicacion() {

        val permisoPreciso =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val permisoAproximado =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (permisoPreciso || permisoAproximado) {
            obtenerUbicacionActual()
            return
        }

        solicitarPermisosUbicacion.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionActual() {

        btnUsarUbicacion.isEnabled = false

        tvEstadoUbicacion.text =
            getString(R.string.ubicacion_obteniendo)

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
            .addOnSuccessListener { location ->

                btnUsarUbicacion.isEnabled = true

                if (location == null) {
                    mostrarErrorUbicacion()
                    return@addOnSuccessListener
                }

                latitud = location.latitude
                longitud = location.longitude

                ubicacionMaps =
                    crearEnlaceGoogleMaps(
                        latitud = location.latitude,
                        longitud = location.longitude
                    )

                mostrarUbicacionGuardada()
            }
            .addOnFailureListener {

                btnUsarUbicacion.isEnabled = true

                mostrarErrorUbicacion()
            }
    }

    private fun crearEnlaceGoogleMaps(
        latitud: Double,
        longitud: Double
    ): String {

        return "https://www.google.com/maps/search/?api=1&query=$latitud,$longitud"
    }

    private fun mostrarUbicacionGuardada() {

        tvEstadoUbicacion.text =
            getString(R.string.ubicacion_agregada)

        btnUsarUbicacion.text =
            getString(R.string.actualizar_ubicacion)
    }

    private fun mostrarUbicacionNoAutorizada() {

        tvEstadoUbicacion.text =
            getString(R.string.ubicacion_permiso_denegado)

        Toast.makeText(
            this,
            getString(R.string.ubicacion_opcional),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun mostrarErrorUbicacion() {

        tvEstadoUbicacion.text =
            getString(R.string.ubicacion_no_disponible)

        Toast.makeText(
            this,
            getString(R.string.ubicacion_activa_gps),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun continuarAConfirmacion() {

        val direccion =
            etDireccion.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val referencia =
            etReferencia.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val telefono =
            etTelefono.text
                ?.toString()
                ?.trim()
                .orEmpty()

        if (!validarDireccion(direccion)) {
            return
        }

        if (!validarTelefono(telefono)) {
            return
        }

        abrirConfirmacion(
            direccion = direccion,
            referencia = referencia,
            telefono = telefono
        )
    }

    private fun validarDireccion(
        direccion: String
    ): Boolean {

        if (direccion.isBlank()) {

            etDireccion.error =
                getString(R.string.error_direccion_vacia)

            etDireccion.requestFocus()

            return false
        }

        return true
    }

    private fun validarTelefono(
        telefono: String
    ): Boolean {

        if (telefono.isBlank()) {

            etTelefono.error =
                getString(R.string.error_telefono_vacio)

            etTelefono.requestFocus()

            return false
        }

        if (telefono.length != TELEFONO_LONGITUD) {

            etTelefono.error =
                getString(R.string.error_telefono_longitud)

            etTelefono.requestFocus()

            return false
        }

        if (!telefono.all { caracter ->
                caracter.isDigit()
            }
        ) {

            etTelefono.error =
                getString(R.string.error_telefono_solo_numeros)

            etTelefono.requestFocus()

            return false
        }

        return true
    }

    private fun abrirConfirmacion(
        direccion: String,
        referencia: String,
        telefono: String
    ) {

        val intentConfirmacion =
            Intent(
                this,
                ActivityConfirmarPedido::class.java
            ).apply {

                putExtra(
                    EXTRA_TIPO_ENTREGA,
                    getString(R.string.tipo_entrega_delivery)
                )

                putExtra(
                    EXTRA_METODO_PAGO,
                    getString(R.string.metodo_pago_contra_entrega)
                )

                putExtra(
                    EXTRA_DIRECCION,
                    direccion
                )

                putExtra(
                    EXTRA_REFERENCIA,
                    referencia
                )

                putExtra(
                    EXTRA_TELEFONO,
                    telefono
                )

                latitud?.let { valor ->
                    putExtra(
                        EXTRA_LATITUD,
                        valor
                    )
                }

                longitud?.let { valor ->
                    putExtra(
                        EXTRA_LONGITUD,
                        valor
                    )
                }

                ubicacionMaps?.let { enlace ->
                    putExtra(
                        EXTRA_UBICACION_MAPS,
                        enlace
                    )
                }
            }

        startActivity(intentConfirmacion)
    }

    companion object {

        private const val TELEFONO_LONGITUD = 9

        const val EXTRA_TIPO_ENTREGA =
            "tipoEntrega"

        const val EXTRA_METODO_PAGO =
            "metodoPago"

        const val EXTRA_DIRECCION =
            "direccion"

        const val EXTRA_REFERENCIA =
            "referencia"

        const val EXTRA_TELEFONO =
            "telefono"

        const val EXTRA_LATITUD =
            "latitud"

        const val EXTRA_LONGITUD =
            "longitud"

        const val EXTRA_UBICACION_MAPS =
            "ubicacionMaps"
    }
}