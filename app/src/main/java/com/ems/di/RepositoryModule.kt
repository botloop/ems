package com.ems.di

import com.ems.data.repository.PcrRepositoryImpl
import com.ems.data.repository.VitalSignsRepositoryImpl
import com.ems.domain.repository.PcrRepository
import com.ems.domain.repository.VitalSignsRepository
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
    abstract fun bindPcrRepository(impl: PcrRepositoryImpl): PcrRepository

    @Binds
    @Singleton
    abstract fun bindVitalSignsRepository(impl: VitalSignsRepositoryImpl): VitalSignsRepository
}
