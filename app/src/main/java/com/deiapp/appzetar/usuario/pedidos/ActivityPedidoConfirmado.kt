
package com.deiapp.appzetar.usuario.pedidos

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.google.android.material.button.MaterialButton

class ActivityPedidoConfirmado : AppCompatActivity() {

    private var reproductor: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(R.layout.activity_pedido_confirmado)

        aplicarInsets()

        val numeroPedido = intent.getLongExtra(
            EXTRA_NUMERO_PEDIDO,
            0L
        )

        val totalPedido = intent.getDoubleExtra(
            EXTRA_TOTAL_PEDIDO,
            0.0
        )

        val ivIcono = findViewById<ImageView>(
            R.id.ivIconoConfirmacion
        )

        val tvNumeroPedido = findViewById<TextView>(
            R.id.tvNumeroPedidoConfirmado
        )

        val tvTotalPedido = findViewById<TextView>(
            R.id.tvTotalPedidoConfirmado
        )

        val btnVolverMenu = findViewById<MaterialButton>(
            R.id.btnVolverMenu
        )

        // Número del pedido
        tvNumeroPedido.text = if (numeroPedido > 0L) {
            getString(
                R.string.pedido_confirmado_numero,
                numeroPedido.toString()
            )
        } else {
            getString(
                R.string.pedido_confirmado_numero_no_disponible
            )
        }

        // Total del pedido en soles
        tvTotalPedido.text = getString(
            R.string.pedido_confirmado_total,
            totalPedido
        )

        // Animación del icono de confirmación
        ivIcono.alpha = 0f
        ivIcono.scaleX = 0.55f
        ivIcono.scaleY = 0.55f

        ivIcono.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(450L)
            .start()

        // Reproducir sonido al mostrar la confirmación
        reproducirSonidoConfirmacion()

        // Botón para volver al menú
        btnVolverMenu.setOnClickListener {
            val intentMenu = Intent(
                this,
                ActivityMenuUsuario::class.java
            )

            intentMenu.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intentMenu)
            finish()
        }
    }

    // =========================================================
    // INSETS
    // =========================================================

    private fun aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    // =========================================================
    // SONIDO DE CONFIRMACIÓN
    // =========================================================

    private fun reproducirSonidoConfirmacion() {
        reproductor = MediaPlayer.create(
            this,
            R.raw.pedido_confirmado
        )

        reproductor?.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.release()

            if (reproductor === mediaPlayer) {
                reproductor = null
            }
        }

        reproductor?.start()
    }

    // =========================================================
    // LIBERAR RECURSOS
    // =========================================================

    override fun onDestroy() {
        reproductor?.release()
        reproductor = null

        super.onDestroy()
    }

    // =========================================================
    // CONSTANTES
    // =========================================================

    private companion object {

        const val EXTRA_NUMERO_PEDIDO =
            "extra_numero_pedido_confirmado"

        const val EXTRA_TOTAL_PEDIDO =
            "extra_total_pedido_confirmado"
    }
}