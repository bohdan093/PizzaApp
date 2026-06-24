package com.core.pizzaapp.feature.pizzalist.domain

class GetPizzaListUseCase(private val repository: PizzaRepository) {
    suspend operator fun invoke(): Result<List<Pizza>> = repository.getPizzas()
}
