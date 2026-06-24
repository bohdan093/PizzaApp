package com.core.pizzaapp.feature.pizzalist.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PizzaListResponseDto(
    val pizzas: List<PizzaDto>,
)
