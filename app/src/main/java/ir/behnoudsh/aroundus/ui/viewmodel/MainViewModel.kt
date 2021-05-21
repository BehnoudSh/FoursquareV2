package ir.behnoudsh.aroundus.ui.viewmodel

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.behnoudsh.aroundus.App
import ir.behnoudsh.aroundus.data.model.LocationLiveData
import ir.behnoudsh.aroundus.data.model.LocationModel
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import ir.behnoudsh.aroundus.data.repository.PlacesRepository
import ir.behnoudsh.aroundus.data.room.FoursquarePlace
import ir.behnoudsh.aroundus.di.component.DiComponent
import ir.behnoudsh.aroundus.utils.DistanceUtils
import ir.behnoudsh.aroundus.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class MainViewModel(placesRepository: PlacesRepository) : ViewModel() {
    var placesRepository: PlacesRepository = placesRepository
    private val distanceUtils: DistanceUtils = DistanceUtils()
    var appStart: Boolean = true

    @Inject
    lateinit var application: Context

    init {
        var apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)
    }

    fun locationChanged(location: LocationModel) {

        if (TextUtils.isEmpty(placesRepository.getLastLocationLat())
            || TextUtils.isEmpty(placesRepository.getLastLocationLong())
        ) {
            placesRepository.prefs.myLocationLat = "0"
            placesRepository.prefs.myLocationLong = "0"
            appStart = false
        }

        var distanceFromOldPlace = distanceUtils.distance(
            location.latitude,
            location.longitude,
            placesRepository.getLastLocationLat().toDouble(),
            placesRepository.getLastLocationLong().toDouble()
        )

        if (distanceFromOldPlace > 100) {
            appStart = false
            message.postValue("در حال بروزرسانی مکان‌های اطراف")

            places.postValue(Resource.clear())

            placesRepository.setOffset(0)
            placesRepository.setLastLocationLat(location.latitude.toString())
            placesRepository.setLastLocationLong(location.longitude.toString())
            deletePlacesFromDB()
            fetchPlaces(
                LocationModel(
                    location.longitude,
                    location.latitude
                ),
                placesRepository.getOffset()
            )

        } else {

            if (appStart) {
                appStart = false

                var datetimeDiff: Long = 0
                datetimeDiff = System.currentTimeMillis() - placesRepository.prefs.lastUpdated

                if (datetimeDiff < 86400000) { //data jadid dar db
                    message.postValue(
                        "شما از مکان قبلی خیلی جابجا نشده‌اید و اطلاعات دریافتی به روز هستند."
                    )

                    GlobalScope.launch(Dispatchers.IO) {
                        places.postValue(Resource.success(placesRepository.getPlacesFromDB() as MutableCollection<FoursquarePlace>))
                    }

                } else { //data ghadimi dar db


                    message.postValue(
                        "شما از مکان قبلی خیلی جابجا نشده‌اید ولی اطلاعات قدیمی است، مکان‌های جدیدی دریافت خواهد شد."
                    )

                    deletePlacesFromDB()
                    placesRepository.setOffset(0)
                    fetchPlaces(
                        LocationModel(
                            location.longitude,
                            location.latitude
                        ),
                        placesRepository.getOffset()
                    )
                }
            }
        }
    }

    private val locationData = LocationLiveData(application)
    fun getLocationData(): LocationLiveData {
        return locationData
    }

    private val places = MutableLiveData<Resource<MutableCollection<FoursquarePlace>>>()
    private val message = MutableLiveData<String>()

    private val placeDetails = MutableLiveData<Resource<FoursquarePlace>>()
    private val compositeDisposable = CompositeDisposable()
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
            mutableListOf()
        resp.response?.groups?.get(0)?.items?.forEach() {
            var address = ""
            it.venue?.location?.formattedAddress?.forEach()
            {
                address += it
                address += "\n"
            }

            var item = FoursquarePlace(
                0,
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

                    if (placesList.response?.groups?.get(0)?.items?.size != 0) {
                        placesRepository.setOffset(placesRepository.getOffset() + 20)
                        placesRepository.setLastUpdatedTime(System.currentTimeMillis())
                        message.postValue("اطلاعات جدید دریافت شد")
                    } else {
                        if (offset == 0) {
                            message.postValue(
                                "مکانی در این موقعیت یافت نشد"
                            )
                            places.postValue(Resource.empty())
                        }
                    }

                    addPlacesToDBAndEmitForView(placesList)

                }, { throwable ->
                    var message = ""
                    message =
                        if (throwable is UnknownHostException || throwable is ConnectException)
                            "اتصال اینترنت را بررسی کنید و دوباره تلاش کنید"
                        else
                            "مشکلی در دریافت اطلاعات به وجود آمد"
                    places.postValue(
                        Resource.error(
                            message,
                            null
                        )
                    )
                })
        )

    }

    fun fetchPlaceDetails(place: FoursquarePlace) {
        placeDetails.postValue(Resource.loading(null))
        compositeDisposable.add(
            placesRepository.getPlaceDetails(place.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Details ->

                    place.link = Details.response?.venue?.canonicalUrl
                    placeDetails.postValue(Resource.success(place))
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

    fun getPlaceDetails(): LiveData<Resource<FoursquarePlace>> {
        return placeDetails
    }

    fun getMessage(): LiveData<String> {
        return message
    }

    private fun deletePlacesFromDB() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.deletePlacesFromDB()
    }

}