package com.deiapp.appzetar.usuario.entregas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.pedidos.ActivityConfirmarPedido
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ActivityDireccion : AppCompatActivity() {

    private lateinit var etDireccion: TextInputEditText
    private lateinit var etReferencia: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var btnContinuar: MaterialButton

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

        btnContinuar =
            findViewById(R.id.btnContinuarDireccion)
    }

    private fun configurarEventos() {
        btnContinuar.setOnClickListener {
            continuarAConfirmacion()
        }
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
    }
}