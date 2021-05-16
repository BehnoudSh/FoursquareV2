package ir.behnoudsh.aroundus.di.component

import dagger.Component
import ir.behnoudsh.aroundus.di.module.ApiHelperModule
import ir.behnoudsh.pixabay.data.repository.PlacesRepository
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiHelperModule::class])
interface ApiHelperComponent {
    fun inject(mainRepository: PlacesRepository)
}
