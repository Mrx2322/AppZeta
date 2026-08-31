package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appzetar.R
import com.example.appzetar.Usuario.Pagos.ActivityPagoYape
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ActivityDireccion : AppCompatActivity() {

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var etDireccion: TextInputEditText
    private lateinit var etReferencia: TextInputEditText
    private lateinit var etTelefono: TextInputEditText

    private lateinit var btnContinuar: MaterialButton


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_direccion
        )


        // =====================================================
        // INICIALIZAR
        // =====================================================

        etDireccion =
            findViewById(R.id.etDireccion)

        etReferencia =
            findViewById(R.id.etReferencia)

        etTelefono =
            findViewById(R.id.etTelefono)

        btnContinuar =
            findViewById(R.id.btnContinuarDireccion)


        // =====================================================
        // CONTINUAR
        // =====================================================

        btnContinuar.setOnClickListener {

            continuarAlPago()
        }
    }


    // =========================================================
    // CONTINUAR AL PAGO
    // =========================================================

    private fun continuarAlPago() {

        val direccion =
            etDireccion.text
                ?.toString()
                ?.trim()
                ?: ""

        val referencia =
            etReferencia.text
                ?.toString()
                ?.trim()
                ?: ""

        val telefono =
            etTelefono.text
                ?.toString()
                ?.trim()
                ?: ""


        // =====================================================
        // VALIDAR DIRECCIÓN
        // =====================================================

        if (direccion.isEmpty()) {

            etDireccion.error =
                "Ingresa tu dirección"

            etDireccion.requestFocus()

            return
        }


        // =====================================================
        // VALIDAR TELÉFONO
        // =====================================================

        if (telefono.isEmpty()) {

            etTelefono.error =
                "Ingresa tu número de teléfono"

            etTelefono.requestFocus()

            return
        }


        if (telefono.length != 9) {

            etTelefono.error =
                "El teléfono debe tener 9 dígitos"

            etTelefono.requestFocus()

            return
        }


        // =====================================================
        // IR AL PAGO
        // =====================================================

        val intent =
            Intent(
                this,
                ActivityPagoYape::class.java
            )


        // =====================================================
        // ENVIAR DATOS AL PAGO
        // =====================================================

        intent.putExtra(
            "tipoEntrega",
            "DELIVERY"
        )

        intent.putExtra(
            "direccion",
            direccion
        )

        intent.putExtra(
            "referencia",
            referencia
        )

        intent.putExtra(
            "telefono",
            telefono
        )


        startActivity(intent)
    }
}