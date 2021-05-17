package ir.behnoudsh.aroundus.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.behnoudsh.aroundus.App
import ir.behnoudsh.aroundus.data.model.LocationLiveData
import ir.behnoudsh.aroundus.data.model.venue.ResponseVenue
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.di.component.DiComponent
import ir.behnoudsh.aroundus.utils.Resource
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class MainViewModel(placesRepository: PlacesRepository) : ViewModel() {
    var placesRepository: PlacesRepository = placesRepository

    @Inject
    lateinit var application: Application

    init {

        var apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)
    }

    private val locationData = LocationLiveData(application)

    private val places = MutableLiveData<Resource<ResponseVenues>>()

    private val placeDetails = MutableLiveData<Resource<ResponseVenue>>()

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun fetchPlaces(lng_lat: String, offset: Int) {
        places.postValue(Resource.loading(null))
        compositeDisposable.add(
            placesRepository.getPlaces(lng_lat, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ imageList ->
                    places.postValue(Resource.success(imageList))
                }, { throwable ->
                    var message = ""
                    message =
                        if (throwable is UnknownHostException || throwable is ConnectException)
                            "check your internet connection and try again!"
                        else
                            "something went wrong. try again!"
                    places.postValue(
                        Resource.error(
                            message,
                            null
                        )
                    )
                })
        )

    }

    fun fetchPlaceDetails(venueId: String) {
        placeDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            placesRepository.getPlaceDetails(venueId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Details ->
                    placeDetails.postValue(Resource.success(Details))
                }, { throwable ->
                    var message = ""
                    message =
                        if (throwable is UnknownHostException || throwable is ConnectException)
                            "check your internet connection and try again!"
                        else
                            "something went wrong. try again!"
                    places.postValue(
                        Resource.error(
                            message,
                            null
                        )
                    )
                })
        )

    }


    fun getPlaces(): LiveData<Resource<ResponseVenues>> {
        return places
    }

    fun getPlaceDetails(): LiveData<Resource<ResponseVenue>> {
        return placeDetails
    }

    fun getLocationData(): LocationLiveData {
        return locationData
    }
}