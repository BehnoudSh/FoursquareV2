package ir.behnoudsh.aroundus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.behnoudsh.aroundus.App
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.di.component.DiComponent
import javax.inject.Inject

class ViewModelFactory() : ViewModelProvider.Factory {
    @Inject
    lateinit var placesRepository: PlacesRepository
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(placesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}