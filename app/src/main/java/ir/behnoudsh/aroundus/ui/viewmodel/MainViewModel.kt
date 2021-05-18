package ir.behnoudsh.aroundus.ui.viewmodel

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.behnoudsh.aroundus.App
import ir.behnoudsh.aroundus.data.model.LocationLiveData
import ir.behnoudsh.aroundus.data.model.LocationModel
import ir.behnoudsh.aroundus.data.model.venue.ResponseVenue
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.data.room.FoursquarePlace
import ir.behnoudsh.aroundus.di.component.DiComponent
import ir.behnoudsh.aroundus.utils.DistanceUtils
import ir.behnoudsh.aroundus.utils.InternetUtils
import ir.behnoudsh.aroundus.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.ContentHandler
import java.net.UnknownHostException
import javax.inject.Inject

class MainViewModel(placesRepository: PlacesRepository) : ViewModel() {
    var placesRepository: PlacesRepository = placesRepository
    val distanceUtils: DistanceUtils = DistanceUtils()

    @Inject
    lateinit var application: Context

    init {
        var apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)
    }

    private val locationData = LocationLiveData(application).observeForever(Observer {

        locationChanged(LocationModel(it.longitude, it.latitude))

    })

    private val places = MutableLiveData<Resource<ResponseVenues>>()
    private val placesFromDB = MutableLiveData<Resource<List<FoursquarePlace>>>()

    private val placeDetails = MutableLiveData<Resource<ResponseVenue>>()

    private val compositeDisposable = CompositeDisposable()
    private val internetUtils: InternetUtils = InternetUtils(application)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun addPlacesToDB(resp: ResponseVenues) {
        var placesList: MutableCollection<FoursquarePlace> =
            mutableListOf<FoursquarePlace>()
        resp.response?.groups?.get(0)?.items?.forEach() {
            var address: String = ""
            it.venue?.location?.formattedAddress?.forEach()
            {
                address += it
                address += "\n"
            }

            var item: FoursquarePlace = FoursquarePlace(
                it.venue?.id,
                it.venue?.name,
                address,
                it.venue?.location?.distance,
                it.venue?.location?.lat.toString(),
                it.venue?.location?.lng.toString(),
                ""
            )
            placesList.add(item)
        }
        GlobalScope.launch(Dispatchers.IO) {
            placesRepository.addPlacesToDB(placesList as ArrayList<FoursquarePlace>)
        }
    }

    private fun fetchPlaces(location: LocationModel, offset: Int) {
        places.postValue(Resource.loading(null))
        compositeDisposable.add(
            placesRepository.getPlaces(
                location.latitude.toString() + "," + location.longitude.toString(),
                offset
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ placesList ->
                    places.postValue(Resource.success(placesList))
                    placesRepository.setOffset(placesRepository.getOffset() + 20)
                    placesRepository.setLastUpdatedTime(System.currentTimeMillis())
                    addPlacesToDB(placesList)
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

    private fun locationChanged(location: LocationModel) {
//        if (TextUtils.isEmpty(placesRepository.getLastLocationLat())
//            || TextUtils.isEmpty(placesRepository.getLastLocationLong())
//        ) {
//            placesRepository.setLastLocationLat(location.latitude.toString())
//            placesRepository.setLastLocationLong(location.longitude.toString())
//        }

        var distanceFromOldPlace = distanceUtils.distance(
            location.latitude,
            location.longitude,
            placesRepository.getLastLocationLat().toDouble(),
            placesRepository.getLastLocationLong().toDouble()
        )
        if (distanceFromOldPlace > 100) {
            placesRepository.setOffset(0)
            placesRepository.setLastLocationLat(location.latitude.toString())
            placesRepository.setLastLocationLong(location.longitude.toString())
            if (internetUtils.isOnline(application)) {
                deletePlacesFromDB()
                fetchPlaces(
                    LocationModel(
                        placesRepository.getLastLocationLong().toDouble(),
                        placesRepository.getLastLocationLat().toDouble()
                    ),
                    placesRepository.getOffset()
                )
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    placesFromDB.postValue(Resource.success(placesRepository.getPlacesFromDB()))
                }
                placesFromDB.postValue(
                    Resource.error(
                        "اینترنت ندارید و از مکان قبلی" + distanceFromOldPlace + "متر جابجا شده‌اید",
                        null
                    )
                )
            }


        } else {

        }

    }

    private fun deletePlacesFromDB() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.deletePlacesFromDB()
    }
}