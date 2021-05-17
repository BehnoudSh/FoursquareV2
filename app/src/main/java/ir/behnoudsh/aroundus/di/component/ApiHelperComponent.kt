package ir.behnoudsh.aroundus.di.component

import dagger.Component
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.di.module.ApiHelperModule
import javax.inject.Singleton


@Singleton
@Component(modules = [ApiHelperModule::class])
interface ApiHelperComponent {

    fun inject(placesRepository: PlacesRepository)


}
