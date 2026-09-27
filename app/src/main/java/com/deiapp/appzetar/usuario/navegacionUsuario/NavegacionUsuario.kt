package com.deiapp.appzetar.usuario.navegacionUsuario

import android.app.Activity
import android.content.Intent
import android.os.Build

object NavegacionUsuario {

    fun abrir(
        activity: Activity,
        destino: Class<out Activity>
    ) {

        // Evita intentar abrir nuevamente
        // la Activity que ya está visible.
        if (activity.javaClass == destino) {
            return
        }

        val intent =
            Intent(
                activity,
                destino
            ).apply {

                /*
                 * Si la pantalla ya existe en la pila:
                 * - la reutiliza;
                 * - evita duplicados;
                 * - elimina las Activities que quedaron
                 *   innecesariamente por encima.
                 */
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        /*
         * Eliminamos las animaciones entre las
         * pantallas principales de la barra inferior.
         *
         * Así se comporta visualmente como una
         * navegación inferior estable y se evita
         * ver dos Activities superpuestas.
         */

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        ) {

            activity.overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                0,
                0
            )

            activity.startActivity(intent)

        } else {

            activity.startActivity(intent)

            @Suppress("DEPRECATION")
            activity.overridePendingTransition(
                0,
                0
            )
        }
    }
}