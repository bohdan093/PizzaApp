package com.core.pizzaapp.feature.splash

import androidx.lifecycle.viewModelScope
import com.core.pizzaapp.core.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SLICE_COUNT = 7
private const val SLICE_INTERVAL_MS = 100L
private const val POST_ANIMATION_PAUSE_MS = 200L

@HiltViewModel
class SplashViewModel @Inject constructor() :
    MviViewModel<SplashState, SplashIntent, SplashEffect>(SplashState) {

    init {
        viewModelScope.launch {
            delay(SLICE_COUNT * SLICE_INTERVAL_MS + POST_ANIMATION_PAUSE_MS)
            postEffect(SplashEffect.NavigateToHome)
        }
    }
}
