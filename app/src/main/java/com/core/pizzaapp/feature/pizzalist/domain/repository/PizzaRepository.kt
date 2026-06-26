package com.core.pizzaapp.feature.pizzalist.domain.repository

import com.core.pizzaapp.feature.pizzalist.domain.model.Pizza

interface PizzaRepository {
    suspend fun getPizzas(): Result<List<Pizza>>
}