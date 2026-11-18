package com.example.lab13_animaciones

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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

@Composable
fun Ejercicio3() {
    var expandido by remember { mutableStateOf(false) }

    // Tamaño animado
    val tamano by animateDpAsState(
        targetValue = if (expandido) 130.dp else 60.dp,
        animationSpec = tween(durationMillis = 800),
        label = "tamano"
    )

    // Posición animada (offset en X e Y)
    val posX by animateDpAsState(
        targetValue = if (expandido) 180.dp else 0.dp,
        animationSpec = tween(durationMillis = 800),
        label = "posX"
    )
    val posY by animateDpAsState(
        targetValue = if (expandido) 200.dp else 0.dp,
        animationSpec = tween(durationMillis = 800),
        label = "posY"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { expandido = !expandido }) {
            Text("Mover y cambiar tamaño")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
                         Box(
                modifier = Modifier
                    .offset(x = posX, y = posY)
                    .size(tamano)
                    .background(Color(0xFFE53935))
            )
        }
    }
}