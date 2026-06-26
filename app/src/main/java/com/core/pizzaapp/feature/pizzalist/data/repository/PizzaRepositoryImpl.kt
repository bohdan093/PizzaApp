package com.core.pizzaapp.feature.pizzalist.data.repository

import com.core.pizzaapp.feature.pizzalist.data.api.PizzaApiService
import com.core.pizzaapp.feature.pizzalist.data.dto.PizzaDto
import com.core.pizzaapp.feature.pizzalist.domain.model.Pizza
import com.core.pizzaapp.feature.pizzalist.domain.repository.PizzaRepository
import com.core.pizzaapp.feature.pizzalist.domain.model.PizzaVariant
import javax.inject.Inject

class PizzaRepositoryImpl @Inject constructor(
    private val apiService: PizzaApiService,
) : PizzaRepository {

    override suspend fun getPizzas(): Result<List<Pizza>> = runCatching {
        apiService.getPizzas().pizzas.map { it.toDomain() }
    }

    private fun PizzaDto.toDomain() = Pizza(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        variants = variants.map { PizzaVariant(size = it.size, price = it.price) },
        defaultSize = defaultSize,
    )
}