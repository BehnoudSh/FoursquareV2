package ir.behnoudsh.aroundus.di.module

import dagger.Module
import dagger.Provides
import ir.behnoudsh.aroundus.data.api.ApiHelper
import javax.inject.Singleton

@Module
class ApiHelperModule {

    @Singleton
    @Provides
    fun providesApiHelper(): ApiHelper {
        return ApiHelper()
    }
}