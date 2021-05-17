package ir.behnoudsh.aroundus.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ir.behnoudsh.aroundus.data.model.LocationLiveData
import ir.behnoudsh.aroundus.data.model.venue.ResponseVenue
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.di.component.DaggerPlacesRepositoryComponent
import ir.behnoudsh.aroundus.di.component.PlacesRepositoryComponent
import ir.behnoudsh.aroundus.utils.Resource
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationData = LocationLiveData(application)

    @Inject
    lateinit var placesRepository: PlacesRepository

    private val places = MutableLiveData<Resource<ResponseVenues>>()
    private val placeDetails = MutableLiveData<Resource<ResponseVenue>>()

    private val compositeDisposable = CompositeDisposable()

    init {
        val placesRepoComponent: PlacesRepositoryComponent =
            DaggerPlacesRepositoryComponent.create()
        placesRepoComponent.inject(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPlaces(): LiveData<Resource<ResponseVenues>> {
        return places
    }

    fun getPlaceDetails(): LiveData<Resource<ResponseVenue>> {
        return placeDetails
    }

    fun getLocationData(): LocationLiveData {
        return locationData;
    }
}