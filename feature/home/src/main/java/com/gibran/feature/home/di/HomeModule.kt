package com.gibran.feature.home.di

import com.gibran.core.database.datasource.TrackDataSource
import com.gibran.feature.home.data.api.HomeApi
import com.gibran.feature.home.data.repository.HomeRepositoryImpl
import com.gibran.feature.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeApi(
        retrofit: Retrofit
    ): HomeApi = retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideHomeRepository(
        api: HomeApi,
        dataSource: TrackDataSource
    ): HomeRepository {
        return HomeRepositoryImpl(api, dataSource)
    }
}
