package com.gibran.core.database.di

import android.content.Context
import androidx.room.Room
import com.gibran.core.database.AppDatabase
import com.gibran.core.database.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    const val NANE_DATABASE = "music_db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            NANE_DATABASE
        ).build()
    }

    @Provides
    fun provideFavoriteTrackDao(db: AppDatabase): TrackDao {
        return db.favoriteTrackDao()
    }
}
