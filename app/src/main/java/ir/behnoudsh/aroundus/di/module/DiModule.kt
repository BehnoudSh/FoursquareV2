package ir.behnoudsh.aroundus.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ir.behnoudsh.aroundus.data.api.ApiHelper
import ir.behnoudsh.aroundus.data.api.ApiService
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class DiModule constructor(private var baseURL: String, private var mApplication: Context) {

    @Singleton
    @Provides
    fun provideOKHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(1200, TimeUnit.SECONDS)
            .connectTimeout(1200, TimeUnit.SECONDS)
            .build()

    }

    @Singleton
    @Provides
    fun provideGSON(): GsonConverterFactory {

        return GsonConverterFactory.create()

    }

    @Singleton
    @Provides
    fun provideRetrofit(): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)

    }

    @Singleton
    @Provides
    fun providesApiHelper(): ApiHelper {
        return ApiHelper()
    }

    @Provides
    @Singleton
    fun providePlacesRepository(): PlacesRepository {
        return PlacesRepository()
    }

    @Provides
    @Singleton
    fun providesApplication(): Context {
        return mApplication
    }

}


