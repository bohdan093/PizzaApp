package com.core.pizzaapp.feature.pizzalist.domain.usecase

import com.core.pizzaapp.feature.pizzalist.domain.model.Pizza
import com.core.pizzaapp.feature.pizzalist.domain.repository.PizzaRepository

class GetPizzaListUseCase(private val repository: PizzaRepository) {
    suspend operator fun invoke(): Result<List<Pizza>> = repository.getPizzas()
}