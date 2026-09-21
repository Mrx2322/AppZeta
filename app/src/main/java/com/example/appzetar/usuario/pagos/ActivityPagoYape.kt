package com.example.appzetar.usuario.pagos

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appzetar.R
import com.example.appzetar.usuario.carrito.PedidoManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ActivityPagoYape : AppCompatActivity() {

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var tvTotal: TextView
    private lateinit var etOperacion: TextInputEditText
    private lateinit var btnYaPague: MaterialButton


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pago_yape)

        initComponent()

        mostrarTotal()

        configurarBoton()
    }


    // =========================================================
    // INICIALIZAR COMPONENTES
    // =========================================================

    private fun initComponent() {

        tvTotal =
            findViewById(R.id.tvTotal)

        etOperacion =
            findViewById(R.id.etOperacion)

        btnYaPague =
            findViewById(R.id.btnYaPague)
    }


    // =========================================================
    // MOSTRAR TOTAL
    // =========================================================

    private fun mostrarTotal() {

        val total =
            PedidoManager.pedido.sumOf {
                it.precio * it.cantidad
            }

        tvTotal.text =
            getString(
                R.string.pago_yape_total_formato,
                total
            )
    }


    // =========================================================
    // BOTÓN YA PAGUÉ
    // =========================================================

    private fun configurarBoton() {

        btnYaPague.setOnClickListener {

            val operacion =
                etOperacion.text
                    ?.toString()
                    ?.trim()
                    ?: ""


            // -------------------------------------------------
            // VALIDAR OPERACIÓN
            // -------------------------------------------------

            if (operacion.isEmpty()) {

                etOperacion.error =
                    getString(
                        R.string.pago_yape_error_operacion
                    )

                etOperacion.requestFocus()

                return@setOnClickListener
            }


            // -------------------------------------------------
            // POR AHORA
            // -------------------------------------------------

            Toast.makeText(
                this,
                getString(
                    R.string.pago_yape_proximamente_mensaje
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}