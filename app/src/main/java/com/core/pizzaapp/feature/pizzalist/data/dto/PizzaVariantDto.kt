package com.core.pizzaapp.feature.pizzalist.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PizzaVariantDto(
    val size: String,
    val price: Double,
)
