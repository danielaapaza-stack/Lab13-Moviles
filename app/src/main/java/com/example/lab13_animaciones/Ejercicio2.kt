package com.example.lab13_animaciones

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun Ejercicio2() {
    var esAzul by remember { mutableStateOf(true) }

    val colorObjetivo = if (esAzul) Color(0xFF1E88E5) else Color(0xFF43A047)

    // Animación con tween: dura un tiempo exacto
    val colorTween by animateColorAsState(
        targetValue = colorObjetivo,
        animationSpec = tween(durationMillis = 1000),
        label = "colorTween"
    )

    // Animación con spring: se basa en "física" y no tiene duración fija
    val colorSpring by animateColorAsState(
        targetValue = colorObjetivo,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "colorSpring"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = { esAzul = !esAzul }) {
            Text("Cambiar color")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Con tween (1 segundo)")
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .size(130.dp)
                .background(colorTween)
        ) {}

        Spacer(modifier = Modifier.height(24.dp))

        Text("Con spring")
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .size(130.dp)
                .background(colorSpring)
        ) {}
    }
}