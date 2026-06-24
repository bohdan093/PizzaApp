package com.core.pizzaapp.feature.pizzalist.data

import com.core.pizzaapp.feature.pizzalist.data.dto.PizzaDto
import com.core.pizzaapp.feature.pizzalist.domain.Pizza
import com.core.pizzaapp.feature.pizzalist.domain.PizzaRepository
import com.core.pizzaapp.feature.pizzalist.domain.PizzaVariant
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
