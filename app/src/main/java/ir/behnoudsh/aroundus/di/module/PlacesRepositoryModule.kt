package ir.behnoudsh.aroundus.di.module

import dagger.Module
import dagger.Provides
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import javax.inject.Singleton

@Module
class PlacesRepositoryModule {

    @Singleton
    @Provides
    fun providesImageRepository(): PlacesRepository {
        return PlacesRepository()
    }
}