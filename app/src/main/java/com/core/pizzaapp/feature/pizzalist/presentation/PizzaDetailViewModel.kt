package com.core.pizzaapp.feature.pizzalist.presentation

import androidx.lifecycle.viewModelScope
import com.core.pizzaapp.core.mvi.MviViewModel
import com.core.pizzaapp.feature.pizzalist.domain.usecase.GetPizzaListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PizzaDetailViewModel @Inject constructor(
    private val getPizzaList: GetPizzaListUseCase,
) : MviViewModel<PizzaDetailState, PizzaDetailIntent, PizzaDetailEffect>(PizzaDetailState()) {

    init {
        viewModelScope.launch {
            intentFlow().collect { intent ->
                when (intent) {
                    PizzaDetailIntent.Retry -> loadPizzas()
                    is PizzaDetailIntent.SelectSize -> handleSelectSize(intent.pizzaId, intent.size)
                    is PizzaDetailIntent.IncreaseQuantity -> handleQuantityChange(intent.pizzaId, +1)
                    is PizzaDetailIntent.DecreaseQuantity -> handleQuantityChange(intent.pizzaId, -1)
                }
            }
        }
        viewModelScope.launch { loadPizzas() }
    }

    private suspend fun loadPizzas() {
        setState { copy(isLoading = true, error = null) }
        getPizzaList()
            .onSuccess { pizzas ->
                val defaultSizes = pizzas.associate { it.id to it.defaultSize }
                setState { copy(pizzas = pizzas, selectedSizes = defaultSizes, isLoading = false) }
            }
            .onFailure { t ->
                val msg = t.message ?: "Failed to load pizzas"
                setState { copy(isLoading = false, error = msg) }
                postEffect(PizzaDetailEffect.ShowError(msg))
            }
    }

    private fun handleSelectSize(pizzaId: String, size: String) {
        setState { copy(selectedSizes = selectedSizes + (pizzaId to size)) }
    }

    private fun handleQuantityChange(pizzaId: String, delta: Int) {
        val current = state.value.quantityFor(pizzaId)
        val next = (current + delta).coerceAtLeast(1)
        setState { copy(quantities = quantities + (pizzaId to next)) }
    }
}
