package com.jera.caracterisiticsv1.di

import com.jera.caracterisiticsv1.data.database.ModelDatabase
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    private val MODEL_DATABASE_NAME = "model_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
         Room.databaseBuilder(context, ModelDatabase::class.java, MODEL_DATABASE_NAME).build()


    @Singleton
    @Provides
    fun provideModelDao(database: ModelDatabase) =
        database.modelDetectedDao()
}