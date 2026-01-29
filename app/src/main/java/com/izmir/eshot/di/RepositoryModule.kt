package com.izmir.eshot.di

import com.izmir.eshot.data.repository.BusStopRepositoryImpl
import com.izmir.eshot.domain.repository.BusStopRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindBusStopRepository(
        busStopRepositoryImpl: BusStopRepositoryImpl
    ): BusStopRepository
}
