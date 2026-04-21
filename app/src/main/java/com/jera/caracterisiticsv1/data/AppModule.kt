package com.jera.caracterisiticsv1.data

import android.util.Log
import com.jera.caracterisiticsv1.BuildConfig
import com.jera.caracterisiticsv1.repository.CameraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

        val braveAuthInterceptor = Interceptor { chain ->
            val request = chain.request()
            val isBrave = request.url.host == "api.search.brave.com"
            val token = BuildConfig.BRAVE_API_KEY

            if (BuildConfig.DEBUG && isBrave) {
                // No logueamos el token completo para no exponerlo en logcat
                val masked = when {
                    token.isEmpty() -> "<EMPTY>"
                    token.length <= 6 -> "***"
                    else -> token.take(3) + "..." + token.takeLast(3)
                }
                Log.d(
                    "BraveAuth",
                    "Applying token to host=${request.url.host} tokenLen=${token.length} tokenMasked=$masked"
                )
            }

            val newRequest = if (isBrave) {
                request.newBuilder()
                    .header("X-Subscription-Token", token)
                    .build()
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
            .addInterceptor(braveAuthInterceptor)
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

}
