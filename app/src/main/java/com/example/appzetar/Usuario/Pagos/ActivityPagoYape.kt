package com.example.appzetar.Usuario.Pagos

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ActivityPagoYape : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var btnYaPague: MaterialButton
    private lateinit var etOperacion: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(R.layout.activity_pago_yape)

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

        initComponent()
        initUI()
    }

    // ---------------------------------------------------------
    // COMPONENTES
    // ---------------------------------------------------------

    private fun initComponent() {

        tvTotal =
            findViewById(R.id.tvTotal)

        btnYaPague =
            findViewById(R.id.btnYaPague)

        etOperacion =
            findViewById(R.id.etOperacion)
    }

    // ---------------------------------------------------------
    // UI
    // ---------------------------------------------------------

    private fun initUI() {

        // Por ahora mostramos un total provisional.
        // Más adelante recibiremos el total real del pedido.

        tvTotal.text =
            "Total: S/ 0.00"

        // -----------------------------------------------------
        // BOTÓN YA REALICÉ EL PAGO
        // -----------------------------------------------------

        btnYaPague.setOnClickListener {

            val operacion =
                etOperacion.text
                    ?.toString()
                    ?.trim()
                    ?: ""

            // ---------------------------------------------
            // VALIDAR NÚMERO DE OPERACIÓN
            // ---------------------------------------------

            if (operacion.isEmpty()) {

                etOperacion.error =
                    "Ingresa el número de operación"

                etOperacion.requestFocus()

                return@setOnClickListener
            }

            // ---------------------------------------------
            // PAGO REPORTADO
            // ---------------------------------------------

            Toast.makeText(
                this,
                "Pago reportado correctamente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}