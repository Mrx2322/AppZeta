package com.deiapp.appzetar.usuario.entregas

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.pedidos.ActivityConfirmarPedido
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ActivityEntrega : AppCompatActivity() {

    private lateinit var cardDelivery: MaterialCardView
    private lateinit var cardRecojo: MaterialCardView

    private lateinit var rbDelivery: RadioButton
    private lateinit var rbRecojo: RadioButton

    private lateinit var cardContraEntrega: MaterialCardView
    private lateinit var cardYape: MaterialCardView

    private lateinit var rbContraEntrega: RadioButton
    private lateinit var rbYape: RadioButton

    private lateinit var btnContinuar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_entrega)

        inicializarVistas()
        configurarInterfaz()
    }

    private fun inicializarVistas() {
        cardDelivery =
            findViewById(R.id.cardDelivery)

        cardRecojo =
            findViewById(R.id.cardRecojo)

        rbDelivery =
            findViewById(R.id.rbDelivery)

        rbRecojo =
            findViewById(R.id.rbRecojo)

        cardContraEntrega =
            findViewById(R.id.cardContraEntrega)

        cardYape =
            findViewById(R.id.cardYape)

        rbContraEntrega =
            findViewById(R.id.rbContraEntrega)

        rbYape =
            findViewById(R.id.rbYape)

        btnContinuar =
            findViewById(R.id.btnContinuarEntrega)
    }

    private fun configurarInterfaz() {
        rbDelivery.isChecked = false
        rbRecojo.isChecked = false

        rbContraEntrega.isChecked = true
        rbYape.isChecked = false

        actualizarSeleccionEntrega()
        actualizarSeleccionPago()

        cardDelivery.setOnClickListener {
            seleccionarDelivery()
        }

        rbDelivery.setOnClickListener {
            seleccionarDelivery()
        }

        cardRecojo.setOnClickListener {
            seleccionarRecojo()
        }

        rbRecojo.setOnClickListener {
            seleccionarRecojo()
        }

        cardContraEntrega.setOnClickListener {
            seleccionarContraEntrega()
        }

        rbContraEntrega.setOnClickListener {
            seleccionarContraEntrega()
        }

        cardYape.setOnClickListener {
            mostrarYapeProximamente()
        }

        rbYape.setOnClickListener {
            mostrarYapeProximamente()
        }

        btnContinuar.setOnClickListener {
            continuar()
        }
    }

    private fun seleccionarDelivery() {
        rbDelivery.isChecked = true
        rbRecojo.isChecked = false

        actualizarSeleccionEntrega()
    }

    private fun seleccionarRecojo() {
        rbDelivery.isChecked = false
        rbRecojo.isChecked = true

        actualizarSeleccionEntrega()
    }

    private fun seleccionarContraEntrega() {
        rbContraEntrega.isChecked = true
        rbYape.isChecked = false

        actualizarSeleccionPago()
    }

    private fun actualizarSeleccionEntrega() {
        cardDelivery.strokeWidth =
            if (rbDelivery.isChecked) {
                STROKE_SELECCIONADO
            } else {
                STROKE_NORMAL
            }

        cardRecojo.strokeWidth =
            if (rbRecojo.isChecked) {
                STROKE_SELECCIONADO
            } else {
                STROKE_NORMAL
            }
    }

    private fun actualizarSeleccionPago() {
        cardContraEntrega.strokeWidth =
            if (rbContraEntrega.isChecked) {
                STROKE_SELECCIONADO
            } else {
                STROKE_NORMAL
            }

        cardYape.strokeWidth =
            STROKE_NORMAL
    }

    private fun mostrarYapeProximamente() {
        rbContraEntrega.isChecked = true
        rbYape.isChecked = false

        actualizarSeleccionPago()

        Toast.makeText(
            this,
            getString(R.string.yape_proximamente),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun continuar() {
        if (!rbContraEntrega.isChecked) {
            mostrarYapeProximamente()
            return
        }

        when {
            rbDelivery.isChecked -> {
                abrirDelivery()
            }

            rbRecojo.isChecked -> {
                abrirRecojo()
            }

            else -> {
                Toast.makeText(
                    this,
                    getString(R.string.selecciona_tipo_entrega),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun abrirDelivery() {
        val intent =
            Intent(
                this,
                ActivityDireccion::class.java
            ).apply {

                putExtra(
                    ActivityDireccion.EXTRA_METODO_PAGO,
                    getString(
                        R.string.metodo_pago_contra_entrega
                    )
                )

                putExtra(
                    ActivityDireccion.EXTRA_TIPO_ENTREGA,
                    getString(
                        R.string.tipo_entrega_delivery
                    )
                )
            }

        startActivity(intent)
    }

    private fun abrirRecojo() {
        val intent =
            Intent(
                this,
                ActivityConfirmarPedido::class.java
            ).apply {

                putExtra(
                    EXTRA_METODO_PAGO,
                    getString(
                        R.string.metodo_pago_contra_entrega
                    )
                )

                putExtra(
                    EXTRA_TIPO_ENTREGA,
                    getString(
                        R.string.tipo_entrega_recojo
                    )
                )
            }

        startActivity(intent)
    }

    companion object {

        private const val STROKE_NORMAL = 0
        private const val STROKE_SELECCIONADO = 2

        private const val EXTRA_METODO_PAGO =
            "metodoPago"

        private const val EXTRA_TIPO_ENTREGA =
            "tipoEntrega"
    }
}