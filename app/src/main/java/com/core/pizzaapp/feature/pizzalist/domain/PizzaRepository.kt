package com.core.pizzaapp.feature.pizzalist.domain

interface PizzaRepository {
    suspend fun getPizzas(): Result<List<Pizza>>
}
