package com.example.lab13_animaciones

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.random.Random

enum class EstadoDino {
    ESPERANDO, JUGANDO, PERDIDO
}

private const val GRAVEDAD = 1500f
private const val VELOCIDAD_SALTO = 560f
private const val SUELO = 30f
private const val DINO_X = 40f
private const val DINO_TAM = 44f
private const val CACTUS_ANCHO = 26f

@Composable
fun EjercicioDino() {
    var estado by remember { mutableStateOf(EstadoDino.ESPERANDO) }
    var dinoY by remember { mutableStateOf(0f) }
    var velY by remember { mutableStateOf(0f) }
    var cactusX by remember { mutableStateOf(1000f) }
    var cactusAlto by remember { mutableStateOf(44f) }
    var puntaje by remember { mutableStateOf(0f) }
    var mejor by remember { mutableStateOf(0) }

    val anchoJuego = LocalConfiguration.current.screenWidthDp - 32f

    // Animación de color: el dino se pone rojo cuando pierde
    val colorDino by animateColorAsState(
        targetValue = if (estado == EstadoDino.PERDIDO) Color(0xFFE53935) else Color(0xFF424242),
        animationSpec = tween(durationMillis = 300),
        label = "colorDino"
    )

    // Animación infinita: las nubes se mueven de derecha a izquierda
    val transicion = rememberInfiniteTransition(label = "nubes")
    val fraccion by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fraccion"
    )
    val nube1X = (anchoJuego + 80f) * (1f - fraccion) - 80f
    val nube2X = (anchoJuego + 80f) * (1f - ((fraccion + 0.5f) % 1f)) - 80f

    // Ciclo del juego: se ejecuta mientras el estado sea JUGANDO
    LaunchedEffect(estado) {
        if (estado == EstadoDino.JUGANDO) {
            var ultimo = withFrameNanos { it }
            while (true) {
                val ahora = withFrameNanos { it }
                val dt = ((ahora - ultimo) / 1_000_000_000f).coerceAtMost(0.05f)
                ultimo = ahora

                // Salto con gravedad
                velY -= GRAVEDAD * dt
                dinoY += velY * dt
                if (dinoY <= 0f) {
                    dinoY = 0f
                    velY = 0f
                }

                // Movimiento del cactus (cada vez más rápido)
                val velocidad = (260f + puntaje * 0.6f).coerceAtMost(520f)
                cactusX -= velocidad * dt
                if (cactusX < -40f) {
                    cactusX = anchoJuego + Random.nextInt(0, 250)
                    cactusAlto = Random.nextInt(32, 58).toFloat()
                }

                puntaje += dt * 10f

                // Colisión
                val choca = cactusX < DINO_X + DINO_TAM - 8f &&
                        cactusX + CACTUS_ANCHO > DINO_X + 8f &&
                        dinoY < cactusAlto - 8f
                if (choca) {
                    mejor = max(mejor, puntaje.toInt())
                    estado = EstadoDino.PERDIDO
                    break
                }
            }
        }
    }

    fun iniciar() {
        dinoY = 0f
        velY = 0f
        cactusX = anchoJuego + 100f
        cactusAlto = 44f
        puntaje = 0f
        estado = EstadoDino.JUGANDO
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cambio de estado con AnimatedContent
        AnimatedContent(
            targetState = estado,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 400)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 200))
            },
            label = "titulo"
        ) { e ->
            Text(
                text = when (e) {
                    EstadoDino.ESPERANDO -> "🦖 Toca para empezar"
                    EstadoDino.JUGANDO -> "🏃 ¡Corre!"
                    EstadoDino.PERDIDO -> "💥 ¡Game Over!"
                },
                fontSize = 26.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Puntaje: ${puntaje.toInt()}     Mejor: $mejor", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))

        // Escenario del juego
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color(0xFFF7F7F7))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (estado == EstadoDino.JUGANDO) {
                        if (dinoY <= 1f) velY = VELOCIDAD_SALTO
                    } else {
                        iniciar()
                    }
                }
        ) {
            // Nubes
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = nube1X.dp, y = 30.dp)
                    .size(width = 60.dp, height = 20.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = nube2X.dp, y = 80.dp)
                    .size(width = 50.dp, height = 16.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            )

            // Suelo
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(SUELO.dp)
                    .background(Color(0xFFD7CCC8))
            )

            // Cactus
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = cactusX.dp, y = -SUELO.dp)
                    .size(width = CACTUS_ANCHO.dp, height = cactusAlto.dp)
                    .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
            )

            // Dino
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = DINO_X.dp, y = -(SUELO + dinoY).dp)
                    .size(DINO_TAM.dp)
                    .background(colorDino, RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(8.dp)
                        .background(Color.White, CircleShape)
                )
            }

            // Panel de Game Over con AnimatedVisibility
            PanelGameOver(
                visible = estado == EstadoDino.PERDIDO,
                puntaje = puntaje.toInt(),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Toca el escenario para saltar", fontSize = 14.sp)
    }
}

@Composable
fun PanelGameOver(visible: Boolean, puntaje: Int, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(400)) +
                scaleIn(initialScale = 0.6f, animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xCC000000), RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GAME OVER", color = Color.White, fontSize = 24.sp)
            Text("Puntaje: $puntaje", color = Color.White)
            Text("Toca para reintentar", color = Color.White, fontSize = 14.sp)
        }
    }
}