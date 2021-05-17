package ir.behnoudsh.aroundus.di.component

import dagger.Component
import ir.behnoudsh.aroundus.data.api.ApiHelper
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.di.module.DiModule
import ir.behnoudsh.aroundus.ui.viewmodel.MainViewModel
import ir.behnoudsh.aroundus.ui.viewmodel.ViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [DiModule::class])
interface DiComponent {
    fun inject(apiHelper: ApiHelper)
    fun inject(placesRepository: PlacesRepository)
    fun inject(viewModelFactory: ViewModelFactory)
    fun inject(mainViewModel: MainViewModel)
}
