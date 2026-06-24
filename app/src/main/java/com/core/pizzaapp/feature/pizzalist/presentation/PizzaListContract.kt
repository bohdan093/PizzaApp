package com.core.pizzaapp.feature.pizzalist.presentation

import androidx.compose.runtime.Immutable
import com.core.pizzaapp.core.mvi.MviEffect
import com.core.pizzaapp.core.mvi.MviIntent
import com.core.pizzaapp.core.mvi.MviState
import com.core.pizzaapp.feature.pizzalist.domain.Pizza

@Immutable
data class PizzaListState(
    val pizzas: List<Pizza> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) : MviState

sealed interface PizzaListIntent : MviIntent {
    data object Retry : PizzaListIntent
}

sealed interface PizzaListEffect : MviEffect {
    data class ShowError(val message: String) : PizzaListEffect
}
