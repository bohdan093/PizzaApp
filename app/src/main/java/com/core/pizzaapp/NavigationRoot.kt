package com.core.pizzaapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.core.pizzaapp.feature.pizzadetail.presentation.PizzaDetailScreen
import com.core.pizzaapp.feature.splash.SplashScreen
import com.core.pizzaapp.navigation.AppRoute

@Composable
fun NavigationRoot() {
    var showSplash by rememberSaveable { mutableStateOf(true) }

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 600)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = 600))
        },
        label = "splash_transition",
    ) { isSplash ->
        if (isSplash) {
            SplashScreen(onNavigateToHome = { showSplash = false })
        } else {
            AppNavigation()
        }
    }
}

@Composable
private fun AppNavigation() {
    val backStack = rememberNavBackStack(AppRoute.Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Home> { PizzaDetailScreen() }
        },
    )
}
