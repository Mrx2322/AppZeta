package com.example.appzetar.Usuario

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appzetar.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ActivityEntrega : AppCompatActivity() {

    // =========================================================
    // ENTREGA
    // =========================================================

    private lateinit var cardDelivery: MaterialCardView
    private lateinit var cardRecojo: MaterialCardView

    private lateinit var rbDelivery: RadioButton
    private lateinit var rbRecojo: RadioButton

    // =========================================================
    // PAGO
    // =========================================================

    private lateinit var cardContraEntrega: MaterialCardView
    private lateinit var cardYape: MaterialCardView

    private lateinit var rbContraEntrega: RadioButton
    private lateinit var rbYape: RadioButton

    // =========================================================
    // BOTÓN
    // =========================================================

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
        // INICIALIZAR ENTREGA
        // =====================================================

        cardDelivery =
            findViewById(R.id.cardDelivery)

        cardRecojo =
            findViewById(R.id.cardRecojo)

        rbDelivery =
            findViewById(R.id.rbDelivery)

        rbRecojo =
            findViewById(R.id.rbRecojo)

        // =====================================================
        // INICIALIZAR PAGO
        // =====================================================

        cardContraEntrega =
            findViewById(R.id.cardContraEntrega)

        cardYape =
            findViewById(R.id.cardYape)

        rbContraEntrega =
            findViewById(R.id.rbContraEntrega)

        rbYape =
            findViewById(R.id.rbYape)

        // =====================================================
        // BOTÓN
        // =====================================================

        btnContinuar =
            findViewById(R.id.btnContinuarEntrega)


        // =====================================================
        // ESTADO INICIAL
        // =====================================================

        rbDelivery.isChecked = false
        rbRecojo.isChecked = false

        // Contra entrega será la opción inicial
        rbContraEntrega.isChecked = true
        rbYape.isChecked = false

        actualizarSeleccionEntrega()
        actualizarSeleccionPago()


        // =====================================================
        // DELIVERY
        // =====================================================

        cardDelivery.setOnClickListener {

            seleccionarDelivery()
        }

        rbDelivery.setOnClickListener {

            seleccionarDelivery()
        }


        // =====================================================
        // RECOJO
        // =====================================================

        cardRecojo.setOnClickListener {

            seleccionarRecojo()
        }

        rbRecojo.setOnClickListener {

            seleccionarRecojo()
        }


        // =====================================================
        // CONTRA ENTREGA
        // =====================================================

        cardContraEntrega.setOnClickListener {

            seleccionarContraEntrega()
        }

        rbContraEntrega.setOnClickListener {

            seleccionarContraEntrega()
        }


        // =====================================================
        // YAPE
        // =====================================================

        cardYape.setOnClickListener {

            mostrarYapeProximamente()
        }

        rbYape.setOnClickListener {

            // No permitimos seleccionar Yape todavía
            rbYape.isChecked = false

            mostrarYapeProximamente()
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

        actualizarSeleccionEntrega()
    }


    // =========================================================
    // SELECCIONAR RECOJO
    // =========================================================

    private fun seleccionarRecojo() {

        rbDelivery.isChecked = false
        rbRecojo.isChecked = true

        actualizarSeleccionEntrega()
    }


    // =========================================================
    // ACTUALIZAR ENTREGA
    // =========================================================

    private fun actualizarSeleccionEntrega() {

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
    // SELECCIONAR CONTRA ENTREGA
    // =========================================================

    private fun seleccionarContraEntrega() {

        rbContraEntrega.isChecked = true
        rbYape.isChecked = false

        actualizarSeleccionPago()
    }


    // =========================================================
    // ACTUALIZAR PAGO
    // =========================================================

    private fun actualizarSeleccionPago() {

        if (rbContraEntrega.isChecked) {

            cardContraEntrega.strokeWidth = 2
            cardYape.strokeWidth = 0

        } else {

            cardContraEntrega.strokeWidth = 0
            cardYape.strokeWidth = 0
        }
    }


    // =========================================================
    // MENSAJE YAPE
    // =========================================================

    private fun mostrarYapeProximamente() {

        Toast.makeText(
            this,
            "Yape estará disponible muy pronto 🚀\nPor ahora puedes pagar contra entrega.",
            Toast.LENGTH_LONG
        ).show()

        // Siempre volvemos a Contra entrega
        rbContraEntrega.isChecked = true
        rbYape.isChecked = false

        actualizarSeleccionPago()
    }


    // =========================================================
    // CONTINUAR
    // =========================================================

    private fun continuar() {

        // -----------------------------------------------------
        // Verificar método de pago
        // -----------------------------------------------------

        if (!rbContraEntrega.isChecked) {

            mostrarYapeProximamente()

            return
        }


        // -----------------------------------------------------
        // DELIVERY
        // -----------------------------------------------------

        if (rbDelivery.isChecked) {

            val intent =
                android.content.Intent(
                    this,
                    ActivityDireccion::class.java
                )

            intent.putExtra(
                "metodoPago",
                "Contra entrega"
            )

            intent.putExtra(
                "tipoEntrega",
                "Delivery"
            )

            startActivity(intent)

            return
        }


        // -----------------------------------------------------
        // RECOJO
        // -----------------------------------------------------

        if (rbRecojo.isChecked) {

            /*
             * Aquí NO abrimos ActivityPagoYape.
             *
             * El siguiente paso para RECOJO lo conectaremos
             * con la pantalla de confirmación del pedido.
             */

            Toast.makeText(
                this,
                "Pago contra entrega seleccionado.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -----------------------------------------------------
        // SIN SELECCIÓN
        // -----------------------------------------------------

        Toast.makeText(
            this,
            "Selecciona cómo quieres recibir tu pedido.",
            Toast.LENGTH_SHORT
        ).show()
    }
}