package com.example.composeapptask.di

import android.content.Context
import android.hardware.SensorManager
import androidx.work.WorkManager
import com.example.composeapptask.api.ApiService
import com.example.composeapptask.api.RetrofitHelper
import com.example.composeapptask.appFeatures.dao.sensorActivity.MedicineReminderDao
import com.example.composeapptask.appFeatures.dao.sensorActivity.SessionDao
import com.example.composeapptask.appFeatures.dao.taskFeature.TaskEntityDao
import com.example.composeapptask.appFeatures.sensorActivity.services.ActivityTrackingService
import com.example.composeapptask.repository.LocalDatabaseTaskFeatureRepository
import com.example.composeapptask.repository.MainRepository
import com.example.composeapptask.repository.MedicineRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
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

    @Singleton
    @Provides
    fun provideMedicineRepository(dao: MedicineReminderDao, sessionDao: SessionDao) =
        MedicineRepository(dao = dao, sessionDao = sessionDao)

    @Provides
    fun provideSensorManager(
        @ApplicationContext context: Context
    ): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}

@Module
@InstallIn(ServiceComponent::class)
abstract class ActivityTrackingServiceModule {

    @Binds
    @ServiceScoped
    abstract fun bindActivityTrackingService(
        activityTrackingService: ActivityTrackingService
    ): ActivityTrackingService
}