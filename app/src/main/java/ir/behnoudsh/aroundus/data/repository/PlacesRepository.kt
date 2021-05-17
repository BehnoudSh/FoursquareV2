package ir.behnoudsh.aroundus.data.repository

import android.app.Application
import android.content.Context
import io.reactivex.Single
import ir.behnoudsh.aroundus.App
import ir.behnoudsh.aroundus.data.api.ApiHelper
import ir.behnoudsh.aroundus.data.model.venue.ResponseVenue
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import ir.behnoudsh.aroundus.data.room.AppDataBase
import ir.behnoudsh.aroundus.data.room.FoursquarePlace
import ir.behnoudsh.aroundus.di.component.DaggerDiComponent
import ir.behnoudsh.aroundus.di.component.DiComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

import javax.inject.Inject

class PlacesRepository {

    @Inject
    lateinit var apiHelper: ApiHelper

    @Inject
    lateinit var application: Context

//    private val foursquarePlacesDao = AppDataBase.getDatabase(application).getFoursquareplacesDao()

    init {
        var apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)

    }

    fun getPlaces(lng_lat: String, offset: Int): Single<ResponseVenues>  {
        return apiHelper.getPlaces(lng_lat, offset)
    }

    fun getPlaceDetails(venueId: String): Single<ResponseVenue>  {
        return apiHelper.getPlaceDetails(venueId)
    }

//    suspend fun getPlacesFromDB(): List<FoursquarePlace> = runBlocking(Dispatchers.Default) {
//        val result = async { foursquarePlacesDao.getPlaces() }.await()
//        return@runBlocking result as List<FoursquarePlace>
//    }
//
//    suspend fun addPlacesToDB(places: List<FoursquarePlace>) {
//        withContext(Dispatchers.IO) {
//            foursquarePlacesDao.insertPlaces(places)
//        }
//    }
//
//    suspend fun deletePlacesFromDB() {
//        withContext(Dispatchers.IO) { foursquarePlacesDao.deletePlaces() }
//    }

}