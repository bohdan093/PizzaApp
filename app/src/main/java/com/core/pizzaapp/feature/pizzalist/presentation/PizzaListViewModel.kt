package com.core.pizzaapp.feature.pizzalist.presentation

import androidx.lifecycle.viewModelScope
import com.core.pizzaapp.core.mvi.MviViewModel
import com.core.pizzaapp.feature.pizzalist.domain.GetPizzaListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PizzaListViewModel @Inject constructor(
    private val getPizzaList: GetPizzaListUseCase,
) : MviViewModel<PizzaListState, PizzaListIntent, PizzaListEffect>(PizzaListState()) {

    init {
        viewModelScope.launch {
            intentFlow().collect { intent ->
                when (intent) {
                    PizzaListIntent.Retry -> loadPizzas()
                }
            }
        }
        viewModelScope.launch { loadPizzas() }
    }

    private suspend fun loadPizzas() {
        setState { copy(isLoading = true, error = null) }
        getPizzaList()
            .onSuccess { pizzas ->
                setState { copy(pizzas = pizzas, isLoading = false) }
            }
            .onFailure { throwable ->
                val message = throwable.message ?: "Failed to load pizzas"
                setState { copy(isLoading = false, error = message) }
                postEffect(PizzaListEffect.ShowError(message))
            }
    }
}
