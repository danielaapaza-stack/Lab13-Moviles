package com.example.lab13_animaciones

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EjercicioFinal() {
    var vidaMonstruo by remember { mutableStateOf(100) }
    var heroeAtacando by remember { mutableStateOf(false) }
    var monstruoGolpeado by remember { mutableStateOf(false) }
    var mostrarDano by remember { mutableStateOf(false) }
    var ocupado by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val victoria = vidaMonstruo <= 0

    // Ejercicio 3: el héroe se desliza hacia el monstruo
    val heroeX by animateDpAsState(
        targetValue = if (heroeAtacando) 100.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "heroeX"
    )

    // Ejercicio 3: la barra de vida cambia de tamaño suavemente
    val anchoVida by animateDpAsState(
        targetValue = (vidaMonstruo * 2).dp,
        animationSpec = tween(durationMillis = 500),
        label = "anchoVida"
    )

    // Ejercicio 2: el monstruo cambia de color al recibir daño
    val colorMonstruo by animateColorAsState(
        targetValue = when {
            monstruoGolpeado -> Color(0xFFE53935)
            victoria -> Color(0xFF9E9E9E)
            else -> Color(0xFF43A047)
        },
        animationSpec = tween(durationMillis = 200),
        label = "colorMonstruo"
    )

    // Animación infinita: el monstruo flota arriba y abajo
    val transicionInfinita = rememberInfiniteTransition(label = "flotar")
    val flotar by transicionInfinita.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flotarY"
    )

    fun atacar() {
        scope.launch {
            ocupado = true
            heroeAtacando = true
            delay(350)
            vidaMonstruo = (vidaMonstruo - 20).coerceAtLeast(0)
            monstruoGolpeado = true
            mostrarDano = true
            heroeAtacando = false
            delay(400)
            monstruoGolpeado = false
            delay(500)
            mostrarDano = false
            ocupado = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ejercicio 4: el estado del juego cambia con AnimatedContent
        AnimatedContent(
            targetState = victoria,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 300))
            },
            label = "estadoJuego"
        ) { gano ->
            Text(
                text = if (gano) "🏆 ¡Victoria!" else "⚔️ ¡Pelea!",
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Vida del monstruo: $vidaMonstruo / 100")
        Spacer(modifier = Modifier.height(8.dp))

        // Barra de vida
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(20.dp)
                .background(Color(0xFFBDBDBD), RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .width(anchoVida)
                    .height(20.dp)
                    .background(Color(0xFFE53935), RoundedCornerShape(10.dp))
            )
        }

        // Escenario de pelea
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Héroe
            Box(
                modifier = Modifier
                    .offset(x = heroeX)
                    .size(80.dp)
                    .background(Color(0xFF1E88E5), RoundedCornerShape(12.dp))
            )

            // Monstruo y texto de daño
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .offset(y = flotar.dp)
                        .size(100.dp)
                        .background(colorMonstruo, RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ejercicio 1: el texto de daño aparece y se desvanece
                AnimatedVisibility(
                    visible = mostrarDano,
                    enter = fadeIn(animationSpec = tween(200)) + slideInVertically(),
                    exit = fadeOut(animationSpec = tween(400))
                ) {
                    Text("-20", fontSize = 26.sp, color = Color(0xFFE53935))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            enabled = !ocupado,
            onClick = {
                if (victoria) {
                    vidaMonstruo = 100
                } else {
                    atacar()
                }
            }
        ) {
            Text(if (victoria) "Reiniciar" else "Atacar")
        }
    }
}