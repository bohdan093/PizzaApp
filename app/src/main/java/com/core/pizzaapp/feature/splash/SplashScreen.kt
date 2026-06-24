package com.core.pizzaapp.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.core.pizzaapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val pizzaSlices = listOf(
    R.drawable.ic_pizza,
    R.drawable.ic_pizza_0,
    R.drawable.ic_pizza_1,
    R.drawable.ic_pizza_2,
    R.drawable.ic_pizza_3,
    R.drawable.ic_pizza_4,
    R.drawable.ic_pizza_5,
    R.drawable.ic_pizza_6,
    R.drawable.ic_pizza_7,
)

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                SplashEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val sliceAlphas = remember { List(pizzaSlices.size) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        sliceAlphas.forEachIndexed { index, animatable ->
            launch {
                delay(index * 100L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.size(300.dp)) {
            pizzaSlices.forEachIndexed { index, resId ->
                Image(
                    painter = painterResource(resId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = sliceAlphas[index].value },
                )
            }
        }
    }
}
