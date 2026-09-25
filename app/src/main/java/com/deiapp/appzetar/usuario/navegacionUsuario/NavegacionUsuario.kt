package com.deiapp.appzetar.usuario.navegacionUsuario

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat

object NavegacionUsuario {

    fun abrir(
        activity: Activity,
        destino: Class<out Activity>
    ) {
        if (activity.javaClass == destino) {
            return
        }

        val intent =
            Intent(
                activity,
                destino
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val animacion =
            ActivityOptionsCompat.makeCustomAnimation(
                activity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

        activity.startActivity(
            intent,
            animacion.toBundle()
        )
    }
}