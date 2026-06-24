package com.core.pizzaapp.feature.pizzalist.domain

data class Pizza(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val variants: List<PizzaVariant>,
    val defaultSize: String,
)
