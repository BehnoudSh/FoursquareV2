package ir.behnoudsh.aroundus.di.component

import dagger.Component
import ir.behnoudsh.aroundus.di.module.PlacesRepositoryModule
import ir.behnoudsh.aroundus.ui.viewmodel.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [PlacesRepositoryModule::class])
interface PlacesRepositoryComponent {
    fun inject(mainViewModel: MainViewModel)
}
