package com.example.appzetar.splash

import android.content.Intent
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.appzetar.MainActivity
import com.example.appzetar.R
import kotlin.math.max

class IntroVideoActivity : AppCompatActivity(),
    TextureView.SurfaceTextureListener {

    private lateinit var videoView: TextureView
    private var reproductor: MediaPlayer? = null
    private var preparado = false
    private var navegando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())

        videoView = TextureView(this).apply {
            surfaceTextureListener = this@IntroVideoActivity
        }

        val contenedor = FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.rgb(11, 20, 32))
            addView(
                videoView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        setContentView(contenedor)
    }

    override fun onSurfaceTextureAvailable(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        val superficie = Surface(surfaceTexture)

        try {
            val archivo = resources.openRawResourceFd(R.raw.intro_appzeta)

            val mediaPlayer = MediaPlayer()
            reproductor = mediaPlayer

            mediaPlayer.setDataSource(
                archivo.fileDescriptor,
                archivo.startOffset,
                archivo.length
            )
            archivo.close()

            mediaPlayer.setSurface(superficie)
            superficie.release()

            mediaPlayer.setOnPreparedListener { player ->
                preparado = true
                ajustarVideo(player.videoWidth, player.videoHeight)
                player.start()
            }

            mediaPlayer.setOnVideoSizeChangedListener { _, videoWidth, videoHeight ->
                ajustarVideo(videoWidth, videoHeight)
            }

            mediaPlayer.setOnCompletionListener {
                abrirMainActivity()
            }

            mediaPlayer.setOnErrorListener { _, _, _ ->
                abrirMainActivity()
                true
            }

            mediaPlayer.prepareAsync()
        } catch (_: Exception) {
            superficie.release()
            abrirMainActivity()
        }
    }

    private fun ajustarVideo(videoWidth: Int, videoHeight: Int) {
        if (videoWidth <= 0 || videoHeight <= 0) return

        videoView.post {
            val anchoPantalla = videoView.width.toFloat()
            val altoPantalla = videoView.height.toFloat()

            if (anchoPantalla <= 0f || altoPantalla <= 0f) return@post

            val escala = max(
                anchoPantalla / videoWidth,
                altoPantalla / videoHeight
            )

            val matriz = Matrix().apply {
                setScale(
                    videoWidth * escala / anchoPantalla,
                    videoHeight * escala / altoPantalla,
                    anchoPantalla / 2f,
                    altoPantalla / 2f
                )
            }

            videoView.setTransform(matriz)
        }
    }

    private fun abrirMainActivity() {
        if (navegando) return
        navegando = true

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStop() {
        super.onStop()
        if (preparado && !navegando) {
            reproductor?.pause()
        }
    }

    override fun onStart() {
        super.onStart()
        if (preparado && !navegando) {
            reproductor?.start()
        }
    }

    override fun onSurfaceTextureSizeChanged(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        reproductor?.let {
            ajustarVideo(it.videoWidth, it.videoHeight)
        }
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        reproductor?.release()
        reproductor = null
        preparado = false
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) = Unit

    override fun onDestroy() {
        reproductor?.release()
        reproductor = null
        super.onDestroy()
    }
}