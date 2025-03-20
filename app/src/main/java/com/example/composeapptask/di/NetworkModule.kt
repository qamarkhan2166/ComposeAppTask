package com.example.composeapptask.di

import com.example.composeapptask.api.ApiService
import com.example.composeapptask.api.RetrofitHelper
import com.example.composeapptask.feature.dao.taskFeature.TaskEntityDao
import com.example.composeapptask.repository.LocalDatabaseTaskFeatureRepository
import com.example.composeapptask.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent ::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RetrofitHelper.BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val originalRequest = chain.request()
                val newUrl = originalRequest.url().newBuilder()
                    // .addQueryParameter("key", RetrofitHelper.API_KEY)
                    .build()
                val newRequest = originalRequest.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    @Singleton
    @Provides
    fun providesApiService( retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepository(apiService: ApiService) = MainRepository(apiService)

    @Singleton
    @Provides
    fun provideLocalDatabaseTaskFeatureRepository(dao: TaskEntityDao) =
        LocalDatabaseTaskFeatureRepository(taskEntityDao = dao,)
}