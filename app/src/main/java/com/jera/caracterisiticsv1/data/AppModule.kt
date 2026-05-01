package com.jera.caracterisiticsv1.data

import android.util.Log
import android.content.Context
import com.jera.caracterisiticsv1.BuildConfig
import com.jera.caracterisiticsv1.repository.CameraRepository
import com.jera.caracterisiticsv1.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {

        val carImagesAuthInterceptor = Interceptor { chain ->
            val request = chain.request()
            val isCarImages = request.url.host.contains("carimagesapi.com")

            val newRequest = if (isCarImages) {
                val newUrl = request.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.CAR_IMAGES_API_KEY)
                    .build()
                if (BuildConfig.DEBUG) {
                    Log.d("CarImagesAuth", "Applying api_key to ${request.url.host}")
                }
                request.newBuilder().url(newUrl).build()
            } else {
                request
            }

            chain.proceed(newRequest)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(carImagesAuthInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.carnet.ai/")
            .client(okHttpClient)
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

    @Singleton
    @Provides
    fun providesLocationRepository(@ApplicationContext context: Context): LocationRepository {
        return LocationRepository(context)
    }

}
