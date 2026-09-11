package com.example.appzetar.Usuario

import android.app.Activity
import android.content.Intent

object NavegacionUsuario {

    fun abrir(
        activity: Activity,
        destino: Class<out Activity>
    ) {
        if (activity.javaClass == destino) {
            return
        }

        val intent = Intent(activity, destino).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}
