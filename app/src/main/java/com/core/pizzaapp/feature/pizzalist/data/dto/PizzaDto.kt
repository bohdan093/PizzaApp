package com.core.pizzaapp.feature.pizzalist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PizzaDto(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("image_url") val imageUrl: String,
    val variants: List<PizzaVariantDto>,
    @SerialName("default_size") val defaultSize: String,
)
