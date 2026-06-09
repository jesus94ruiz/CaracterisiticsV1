package com.jera.caracterisiticsv1.di

import com.jera.caracterisiticsv1.data.database.ModelDatabase
import com.jera.caracterisiticsv1.data.database.dao.AchievementDao
import com.jera.caracterisiticsv1.data.database.dao.DailyMissionDao
import com.jera.caracterisiticsv1.data.database.dao.UserProfileDao
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
        Room.databaseBuilder(context, ModelDatabase::class.java, MODEL_DATABASE_NAME)
            .addMigrations(
                ModelDatabase.MIGRATION_2_3,
                ModelDatabase.MIGRATION_3_4,
                ModelDatabase.MIGRATION_4_5,
                ModelDatabase.MIGRATION_5_6
            )
            .fallbackToDestructiveMigration()
            .build()


    @Singleton
    @Provides
    fun provideModelDao(database: ModelDatabase) =
        database.modelDetectedDao()

    @Singleton
    @Provides
    fun provideUserProfileDao(database: ModelDatabase): UserProfileDao =
        database.userProfileDao()

    @Singleton
    @Provides
    fun provideAchievementDao(database: ModelDatabase): AchievementDao =
        database.achievementDao()

    @Singleton
    @Provides
    fun provideDailyMissionDao(database: ModelDatabase): DailyMissionDao =
        database.dailyMissionDao()
}
