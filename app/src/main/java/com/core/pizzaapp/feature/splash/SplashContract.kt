package com.core.pizzaapp.feature.splash

import androidx.compose.runtime.Immutable
import com.core.pizzaapp.core.mvi.MviEffect
import com.core.pizzaapp.core.mvi.MviIntent
import com.core.pizzaapp.core.mvi.MviState

@Immutable
data object SplashState : MviState

sealed interface SplashIntent : MviIntent

sealed interface SplashEffect : MviEffect {
    data object NavigateToHome : SplashEffect
}
