package com.gibran.feature.search.di

import com.gibran.feature.search.data.api.SearchApi
import com.gibran.feature.search.data.repository.SearchRepositoryImpl
import com.gibran.feature.search.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
class SearchModule {

    @Provides
    @Singleton
    fun provideSearchApi(
        retrofit: Retrofit
    ): SearchApi = retrofit.create(SearchApi::class.java)

    @Provides
    @Singleton
    fun provideSearchRepository(
        api: SearchApi
    ): SearchRepository = SearchRepositoryImpl(api)

}
