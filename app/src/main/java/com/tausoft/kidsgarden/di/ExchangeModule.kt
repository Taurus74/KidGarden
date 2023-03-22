package com.tausoft.kidsgarden.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tausoft.kidsgarden.network.WorkCalendarDeserializer
import com.tausoft.kidsgarden.network.WorkCalendarRepository
import com.tausoft.kidsgarden.network.WorkCalendarService
import com.tausoft.kidsgarden.workCalendar.WorkCalendar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExchangeModule {
    const val TAG = "Exchange"

    @Provides
    fun providesBaseUrl() : String = "http://xmlcalendar.ru/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message -> Log.d(TAG, "OkHttp: $message") }
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .connectionPool(
                ConnectionPool(0, 1, TimeUnit.NANOSECONDS)
            )
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .registerTypeAdapter(WorkCalendar::class.java, WorkCalendarDeserializer())
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL : String, client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideXMLCalendarService(retrofit: Retrofit): WorkCalendarService {
        return retrofit.create(WorkCalendarService::class.java)
    }

    @Provides
    @Singleton
    fun provideData(service: WorkCalendarService)
    : WorkCalendarRepository = WorkCalendarRepository(service)
}