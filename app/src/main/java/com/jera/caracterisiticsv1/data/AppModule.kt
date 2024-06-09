package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.repository.CameraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.carnet.ai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitService(retrofit: Retrofit): RetrofitService {
        return retrofit.create(RetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun providesCameraDataSource(retrofitService: RetrofitService): CameraDataSource{
        return CameraDataSourceImpl(retrofitService)
    }

    @Singleton
    @Provides
    fun providesCameraRepository(cameraDataSource: CameraDataSource): CameraRepository{
        return CameraRepository(cameraDataSource)
    }
}
