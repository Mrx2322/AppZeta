package com.deiapp.appzetar.usuario.pedidos

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.deiapp.appzeta.R
import com.deiapp.appzetar.usuario.ActivityMenuUsuario
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class ActivityPedidoConfirmado : AppCompatActivity() {

    private var reproductorConfirmacion: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_pedido_confirmado)
        aplicarInsets()
        reproducirSonidoConfirmacion()
        animarConfirmacion()

        val totalEstimado = intent.getDoubleExtra(EXTRA_TOTAL_ESTIMADO, 0.0)

        findViewById<TextView>(R.id.tvTotalPedidoConfirmado).text =
            getString(R.string.solicitud_total_estimado, totalEstimado)

        findViewById<MaterialButton>(R.id.btnVolverMenu).setOnClickListener {
            startActivity(Intent(this, ActivityMenuUsuario::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun reproducirSonidoConfirmacion() {
        reproductorConfirmacion = MediaPlayer.create(this, R.raw.pedido_confirmado)?.apply {
            setOnCompletionListener { reproductor ->
                reproductor.release()
                if (reproductorConfirmacion === reproductor) {
                    reproductorConfirmacion = null
                }
            }
            start()
        }
    }

    private fun animarConfirmacion() {
        val contenido = findViewById<ScrollView>(R.id.scrollConfirmacion)
        val icono = findViewById<View>(R.id.ivIconoConfirmacion)

        contenido.alpha = 0f
        contenido.translationY = dp(18f)
        contenido.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(420L)
            .setInterpolator(DecelerateInterpolator())
            .start()

        icono.scaleX = 0.55f
        icono.scaleY = 0.55f
        icono.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(120L)
            .setDuration(500L)
            .setInterpolator(OvershootInterpolator(1.35f))
            .start()

        val root = findViewById<FrameLayout>(R.id.main)
        root.postDelayed({ lanzarConfeti(root, icono) }, 180L)
    }

    private fun lanzarConfeti(root: FrameLayout, icono: View) {
        if (isFinishing || isDestroyed || root.width == 0 || root.height == 0) return

        val rootLocation = IntArray(2)
        val iconLocation = IntArray(2)
        root.getLocationOnScreen(rootLocation)
        icono.getLocationOnScreen(iconLocation)

        val centroX = iconLocation[0] - rootLocation[0] + icono.width / 2f
        val centroY = iconLocation[1] - rootLocation[1] + icono.height / 2f
        val confeti = ConfettiOverlayView(this)

        root.addView(
            confeti,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        confeti.start(centroX, centroY)
    }

    private fun dp(value: Float): Float =
        value * resources.displayMetrics.density

    override fun onDestroy() {
        reproductorConfirmacion?.let { reproductor ->
            reproductor.setOnCompletionListener(null)
            reproductor.release()
        }
        reproductorConfirmacion = null
        super.onDestroy()
    }

    private companion object {
        const val EXTRA_TOTAL_ESTIMADO = "extra_total_estimado"
    }
}

private class ConfettiOverlayView(context: android.content.Context) : View(context) {

    private data class Piece(
        val velocityX: Float,
        val velocityY: Float,
        val gravity: Float,
        val width: Float,
        val height: Float,
        val color: Int,
        val rotation: Float,
        val rotationSpeed: Float,
        val isCircle: Boolean
    )

    private val density = resources.displayMetrics.density
    private val random = Random(SystemClock.uptimeMillis())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colors = intArrayOf(
        Color.rgb(232, 117, 32),
        Color.rgb(0, 190, 130),
        Color.rgb(255, 193, 7),
        Color.rgb(244, 91, 105),
        Color.rgb(65, 145, 230),
        Color.rgb(171, 103, 210)
    )
    private val pieces = List(58) {
        Piece(
            velocityX = random.nextFloat() * 440f * density - 220f * density,
            velocityY = -random.nextFloat() * 230f * density - 70f * density,
            gravity = (500f + random.nextFloat() * 420f) * density,
            width = (4f + random.nextFloat() * 5f) * density,
            height = (6f + random.nextFloat() * 7f) * density,
            color = colors[random.nextInt(colors.size)],
            rotation = random.nextFloat() * 360f,
            rotationSpeed = random.nextFloat() * 720f - 360f,
            isCircle = random.nextInt(4) == 0
        )
    }

    private var originX = 0f
    private var originY = 0f
    private var startedAt = 0L
    private val durationMs = 1_700L

    init {
        isClickable = false
        isFocusable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    fun start(x: Float, y: Float) {
        originX = x
        originY = y
        startedAt = SystemClock.uptimeMillis()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val elapsedMs = SystemClock.uptimeMillis() - startedAt
        val seconds = elapsedMs / 1_000f
        val progress = (elapsedMs.toFloat() / durationMs).coerceIn(0f, 1f)
        val alpha = ((1f - progress) * 255).toInt().coerceIn(0, 255)

        pieces.forEach { piece ->
            val x = originX + piece.velocityX * seconds
            val y = originY + piece.velocityY * seconds +
                0.5f * piece.gravity * seconds * seconds

            if (x < -40f * density || x > width + 40f * density ||
                y < -40f * density || y > height + 40f * density
            ) return@forEach

            paint.color = piece.color
            paint.alpha = alpha
            canvas.save()
            canvas.rotate(piece.rotation + piece.rotationSpeed * seconds, x, y)

            if (piece.isCircle) {
                canvas.drawCircle(x, y, piece.width / 2f, paint)
            } else {
                canvas.drawRect(
                    x - piece.width / 2f,
                    y - piece.height / 2f,
                    x + piece.width / 2f,
                    y + piece.height / 2f,
                    paint
                )
            }

            canvas.restore()
        }

        if (elapsedMs < durationMs) {
            postInvalidateOnAnimation()
        } else {
            post { visibility = GONE }
        }
    }
}
