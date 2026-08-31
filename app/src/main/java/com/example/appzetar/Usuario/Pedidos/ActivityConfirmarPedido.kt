package com.example.appzetar.Usuario

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appzetar.R
import com.example.appzetar.Usuario.Pagos.ActivityPagoYape
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityConfirmarPedido : AppCompatActivity() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private val auth =
        FirebaseAuth.getInstance()

    private val db =
        FirebaseFirestore.getInstance()


    // =========================================================
    // COMPONENTES
    // =========================================================

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvCorreoUsuario: TextView

    private lateinit var tvResumenProductos: TextView
    private lateinit var tvTotalPedido: TextView

    private lateinit var radioGroupEntrega: RadioGroup
    private lateinit var radioDelivery: RadioButton
    private lateinit var radioRecojo: RadioButton

    private lateinit var btnContinuarPago: MaterialButton


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(
            R.layout.activity_confirmar_pedido
        )


        // =====================================================
        // INSETS
        // =====================================================

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


        // =====================================================
        // INICIALIZAR
        // =====================================================

        initComponent()
        cargarDatosUsuario()
        cargarResumenPedido()
        configurarEntrega()
        configurarBoton()
    }


    // =========================================================
    // COMPONENTES
    // =========================================================

    private fun initComponent() {

        tvNombreUsuario =
            findViewById(
                R.id.tvNombreUsuario
            )

        tvCorreoUsuario =
            findViewById(
                R.id.tvCorreoUsuario
            )

        tvResumenProductos =
            findViewById(
                R.id.tvResumenProductos
            )

        tvTotalPedido =
            findViewById(
                R.id.tvTotalPedido
            )

        radioGroupEntrega =
            findViewById(
                R.id.radioGroupEntrega
            )

        radioDelivery =
            findViewById(
                R.id.radioDelivery
            )

        radioRecojo =
            findViewById(
                R.id.radioRecojo
            )

        btnContinuarPago =
            findViewById(
                R.id.btnContinuarPago
            )
    }


    // =========================================================
    // USUARIO
    // =========================================================

    private fun cargarDatosUsuario() {

        val usuario =
            auth.currentUser


        if (usuario == null) {

            Toast.makeText(
                this,
                "No hay una sesión activa",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        tvCorreoUsuario.text =
            usuario.email ?: ""


        db.collection("usuarios")
            .document(usuario.uid)
            .get()
            .addOnSuccessListener { documento ->

                if (documento.exists()) {

                    val nombre =
                        documento
                            .getString("nombre")
                            ?: ""


                    tvNombreUsuario.text =
                        nombre
                }
            }
    }


    // =========================================================
    // RESUMEN DEL PEDIDO
    // =========================================================

    private fun cargarResumenPedido() {

        val pedido =
            PedidoManager.pedido


        if (pedido.isEmpty()) {

            finish()

            return
        }


        val cantidad =
            PedidoManager.cantidadTotal()


        tvResumenProductos.text =
            if (cantidad == 1) {
                "1 producto"
            } else {
                "$cantidad productos"
            }


        val total =
            pedido.sumOf {

                it.precio * it.cantidad
            }


        tvTotalPedido.text =
            "S/ %.2f".format(total)
    }


    // =========================================================
    // ENTREGA
    // =========================================================

    private fun configurarEntrega() {

        radioGroupEntrega.setOnCheckedChangeListener {
                _,
                checkedId ->

            when (checkedId) {

                R.id.radioDelivery -> {

                    Toast.makeText(
                        this,
                        "Delivery seleccionado",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                R.id.radioRecojo -> {

                    Toast.makeText(
                        this,
                        "Recojo en tienda seleccionado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    // =========================================================
    // BOTÓN CONTINUAR
    // =========================================================

    private fun configurarBoton() {

        btnContinuarPago.setOnClickListener {

            val opcionSeleccionada =
                radioGroupEntrega.checkedRadioButtonId


            if (
                opcionSeleccionada ==
                -1
            ) {

                Toast.makeText(
                    this,
                    "Selecciona cómo deseas recibir tu pedido",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val tipoEntrega =
                when (
                    opcionSeleccionada
                ) {

                    R.id.radioDelivery ->
                        "Delivery"

                    R.id.radioRecojo ->
                        "Recojo en tienda"

                    else ->
                        ""
                }


            // =================================================
            // GUARDAMOS TEMPORALMENTE LA ELECCIÓN
            // =================================================

            val intent =
                Intent(
                    this,
                    ActivityPagoYape::class.java
                )

            intent.putExtra(
                "tipoEntrega",
                tipoEntrega
            )

            startActivity(intent)
        }
    }
}