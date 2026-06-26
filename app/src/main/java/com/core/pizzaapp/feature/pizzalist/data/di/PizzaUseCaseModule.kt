package com.core.pizzaapp.feature.pizzalist.data.di

import com.core.pizzaapp.feature.pizzalist.domain.usecase.GetPizzaListUseCase
import com.core.pizzaapp.feature.pizzalist.domain.repository.PizzaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PizzaUseCaseModule {

    @Provides
    fun provideGetPizzaListUseCase(repository: PizzaRepository): GetPizzaListUseCase =
        GetPizzaListUseCase(repository)
}
