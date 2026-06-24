package com.core.pizzaapp.feature.pizzalist.data.di

import com.core.pizzaapp.feature.pizzalist.data.PizzaRepositoryImpl
import com.core.pizzaapp.feature.pizzalist.domain.PizzaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PizzaDataModule {

    @Binds
    @Singleton
    fun bindPizzaRepository(impl: PizzaRepositoryImpl): PizzaRepository
}
