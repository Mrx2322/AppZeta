package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.appzetar.R
import com.example.appzetar.Usuario.Pagos.ActivityPagoYape
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlin.jvm.java

class ActivityEntrega : AppCompatActivity() {

    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var cardDelivery: MaterialCardView
    private lateinit var cardRecojo: MaterialCardView

    private lateinit var rbDelivery: RadioButton
    private lateinit var rbRecojo: RadioButton

    private lateinit var btnContinuar: MaterialButton


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_entrega
        )


        // =====================================================
        // INICIALIZAR COMPONENTES
        // =====================================================

        cardDelivery =
            findViewById(R.id.cardDelivery)

        cardRecojo =
            findViewById(R.id.cardRecojo)

        rbDelivery =
            findViewById(R.id.rbDelivery)

        rbRecojo =
            findViewById(R.id.rbRecojo)

        btnContinuar =
            findViewById(R.id.btnContinuarEntrega)


        // =====================================================
        // ESTADO INICIAL
        // =====================================================

        rbDelivery.isChecked = false
        rbRecojo.isChecked = false

        cardDelivery.strokeWidth = 0
        cardRecojo.strokeWidth = 0


        // =====================================================
        // SELECCIONAR DELIVERY
        // =====================================================

        cardDelivery.setOnClickListener {

            seleccionarDelivery()
        }

        rbDelivery.setOnClickListener {

            seleccionarDelivery()
        }


        // =====================================================
        // SELECCIONAR RECOJO
        // =====================================================

        cardRecojo.setOnClickListener {

            seleccionarRecojo()
        }

        rbRecojo.setOnClickListener {

            seleccionarRecojo()
        }


        // =====================================================
        // CONTINUAR
        // =====================================================

        btnContinuar.setOnClickListener {

            continuar()
        }
    }


    // =========================================================
    // SELECCIONAR DELIVERY
    // =========================================================

    private fun seleccionarDelivery() {

        rbDelivery.isChecked = true
        rbRecojo.isChecked = false

        actualizarSeleccion()
    }


    // =========================================================
    // SELECCIONAR RECOJO
    // =========================================================

    private fun seleccionarRecojo() {

        rbDelivery.isChecked = false
        rbRecojo.isChecked = true

        actualizarSeleccion()
    }


    // =========================================================
    // ACTUALIZAR SELECCIÓN
    // =========================================================

    private fun actualizarSeleccion() {

        if (rbDelivery.isChecked) {

            cardDelivery.strokeWidth = 2
            cardRecojo.strokeWidth = 0

        } else if (rbRecojo.isChecked) {

            cardDelivery.strokeWidth = 0
            cardRecojo.strokeWidth = 2

        } else {

            cardDelivery.strokeWidth = 0
            cardRecojo.strokeWidth = 0
        }
    }


    // =========================================================
    // CONTINUAR
    // =========================================================

    private fun continuar() {

        // -----------------------------------------------------
        // DELIVERY
        // -----------------------------------------------------

        if (rbDelivery.isChecked) {

            val intent =
                Intent(
                    this,
                    ActivityDireccion::class.java
                )

            startActivity(intent)

            return
        }


        // -----------------------------------------------------
        // RECOJO
        // -----------------------------------------------------

        if (rbRecojo.isChecked) {

            val intent =
                Intent(
                    this,
                    ActivityPagoYape::class.java
                )

            startActivity(intent)

            return
        }
    }
}