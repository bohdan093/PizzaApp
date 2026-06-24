package com.core.pizzaapp.feature.pizzadetail.presentation

import androidx.compose.runtime.Immutable
import com.core.pizzaapp.core.mvi.MviEffect
import com.core.pizzaapp.core.mvi.MviIntent
import com.core.pizzaapp.core.mvi.MviState
import com.core.pizzaapp.feature.pizzalist.domain.Pizza

@Immutable
data class PizzaDetailState(
    val pizzas: List<Pizza> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedSizes: Map<String, String> = emptyMap(),
    val quantities: Map<String, Int> = emptyMap(),
) : MviState {
    fun selectedSizeFor(pizzaId: String, defaultSize: String) =
        selectedSizes[pizzaId] ?: defaultSize
    fun quantityFor(pizzaId: String) = quantities[pizzaId] ?: 1
}

sealed interface PizzaDetailIntent : MviIntent {
    data object Retry : PizzaDetailIntent
    data class SelectSize(val pizzaId: String, val size: String) : PizzaDetailIntent
    data class IncreaseQuantity(val pizzaId: String) : PizzaDetailIntent
    data class DecreaseQuantity(val pizzaId: String) : PizzaDetailIntent
}

sealed interface PizzaDetailEffect : MviEffect {
    data class ShowError(val message: String) : PizzaDetailEffect
}
