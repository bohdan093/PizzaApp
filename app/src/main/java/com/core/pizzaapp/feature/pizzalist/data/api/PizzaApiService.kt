package com.core.pizzaapp.feature.pizzalist.data.api

import com.core.pizzaapp.feature.pizzalist.data.dto.PizzaListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val BASE_URL = "https://oursongapp.com"

class PizzaApiService(private val httpClient: HttpClient) {
    suspend fun getPizzas(): PizzaListResponseDto =
        httpClient.get("$BASE_URL/api/pizzas").body()
}
