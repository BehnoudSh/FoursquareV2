package ir.behnoudsh.aroundus.ui.viewmodel

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

    private val places = MutableLiveData<Resource<MutableCollection<FoursquarePlace>>>()

    private val placesFromDB = MutableLiveData<Resource<MutableCollection<FoursquarePlace>>>()

    private val placeDetails = MutableLiveData<Resource<ResponseVenue>>()

    private val compositeDisposable = CompositeDisposable()
    private val internetUtils: InternetUtils = InternetUtils(application)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun loadMore() {

        fetchPlaces(
            LocationModel(
                placesRepository.prefs.myLocationLong.toDouble(),
                placesRepository.prefs.myLocationLat.toDouble()
            ),
            placesRepository.prefs.offset
        )

    }

    private fun addPlacesToDBAndEmitForView(resp: ResponseVenues) {
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
                it.venue!!.id,
                it.venue?.name,
                address,
                it.venue?.location?.distance,
                it.venue?.location?.lat.toString(),
                it.venue?.location?.lng.toString(),
                ""
            )
            placesList.add(item)

        }
        places.postValue(Resource.success(placesList))

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
                    placesRepository.setOffset(placesRepository.getOffset() + 20)
                    placesRepository.setLastUpdatedTime(System.currentTimeMillis())
                    addPlacesToDBAndEmitForView(placesList)
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

    fun fetchPlaceDetails(venueId: String?) {
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
                    placeDetails.postValue(
                        Resource.error(
                            message,
                            null
                        )
                    )
                })
        )

    }

    fun getPlaces(): LiveData<Resource<MutableCollection<FoursquarePlace>>> {
        return places
    }

    fun getPlacesFromDB(): LiveData<Resource<MutableCollection<FoursquarePlace>>> {
        return placesFromDB
    }

    fun getPlaceDetails(): LiveData<Resource<ResponseVenue>> {
        return placeDetails
    }

    private fun locationChanged(location: LocationModel) {
        if (TextUtils.isEmpty(placesRepository.getLastLocationLat())
            || TextUtils.isEmpty(placesRepository.getLastLocationLong())
        ) {
            placesRepository.prefs.myLocationLat = "0"
            placesRepository.prefs.myLocationLong = "0"
        }
        var distanceFromOldPlace = distanceUtils.distance(
            location.latitude,
            location.longitude,
            placesRepository.getLastLocationLat().toDouble(),
            placesRepository.getLastLocationLong().toDouble()
        )

        if (distanceFromOldPlace > 100) { //door shodim

            val message: String =
                " اینترنت ندارید و از مکان قبلی" + distanceFromOldPlace + "متر جابجا شده‌اید"

            fetchFromFirst(
                location, message
            )

        } else { //nazdikim hanooz

            var datetimeDiff: Long = 0
            datetimeDiff = System.currentTimeMillis() - placesRepository.prefs.lastUpdated

            if (datetimeDiff < 86400000) { //data jadide

                GlobalScope.launch(Dispatchers.IO) {
                    placesFromDB.postValue(Resource.success(placesRepository.getPlacesFromDB() as MutableCollection<FoursquarePlace>))
                }

            } else { //data ghadimie

                val message: String =
                    if (datetimeDiff < 86400000)
                        "اینترنت ندارید و آخرین اطلاعات دریافتی مربوط به امروز است."
                    else
                        "اینترنت ندارید و آخرین اطلاعات دریافتی مربوط به روزهای پیشین است."

                fetchFromFirst(location, message)
            }
        }
    }

    private fun deletePlacesFromDB() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.deletePlacesFromDB()
    }

    private fun fetchFromFirst(location: LocationModel, message: String) {

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
                placesFromDB.postValue(Resource.success(placesRepository.getPlacesFromDB() as MutableCollection<FoursquarePlace>))
            }

            placesFromDB.postValue(
                Resource.message(
                    message, null
                )
            )
        }

    }
}