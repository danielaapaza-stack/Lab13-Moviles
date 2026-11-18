package com.example.lab13_animaciones

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Estado {
    CARGANDO, CONTENIDO, ERROR
}

@Composable
fun Ejercicio4() {
    var estado by remember { mutableStateOf(Estado.CARGANDO) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                estado = when (estado) {
                    Estado.CARGANDO -> Estado.CONTENIDO
                    Estado.CONTENIDO -> Estado.ERROR
                    Estado.ERROR -> Estado.CARGANDO
                }
            }
        ) {
            Text("Cambiar estado")
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = estado,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 700, delayMillis = 300)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 300))
            },
            label = "contenidoAnimado"
        ) { estadoActual ->
            when (estadoActual) {
                Estado.CARGANDO -> CajaEstado("⏳ Cargando...", Color(0xFFFFA726))
                Estado.CONTENIDO -> CajaEstado("✅ Contenido listo", Color(0xFF43A047))
                Estado.ERROR -> CajaEstado("❌ Ocurrió un error", Color(0xFFE53935))
            }
        }
    }
}

@Composable
fun CajaEstado(mensaje: String, color: Color) {
    Box(
        modifier = Modifier
            .size(width = 280.dp, height = 160.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = mensaje, fontSize = 22.sp, color = Color.White)
    }
}