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
import ir.behnoudsh.aroundus.data.room.FoursquarePlacesDao
import ir.behnoudsh.aroundus.data.sharedpreferences.Prefs
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

    lateinit var prefs: Prefs

    lateinit var foursquarePlacesDao: FoursquarePlacesDao

    init {
        var apiComponent: DiComponent = App.diComponent
        apiComponent.inject(this)
        prefs = Prefs(application)
        foursquarePlacesDao = AppDataBase.getDatabase(application).getFoursquareplacesDao()
    }

    //region api
    fun getPlaces(lng_lat: String, offset: Int): Single<ResponseVenues> {
        return apiHelper.getPlaces(lng_lat, offset)
    }

    fun getPlaceDetails(venueId: String?): Single<ResponseVenue> {
        return apiHelper.getPlaceDetails(venueId)
    }
    //endregion

    //region shared preferences
    fun getLastLocationLat(): String {
        return prefs.myLocationLat
    }

    fun setLastLocationLat(lat: String) {
        prefs.myLocationLat = lat
    }

    fun getLastLocationLong(): String {
        return prefs.myLocationLong
    }

    fun setLastLocationLong(long: String) {
        prefs.myLocationLong = long

    }

    fun getOffset(): Int {
        return prefs.offset
    }

    fun setOffset(offset: Int) {
        prefs.offset = offset
    }

    fun getLastUpdatedTime(): Long {
        return prefs.lastUpdated
    }

    fun setLastUpdatedTime(time: Long) {
        prefs.lastUpdated = time
    }
    //endregion

    //region room local db
    suspend fun getPlacesFromDB(): List<FoursquarePlace> = runBlocking(Dispatchers.Default) {
        val result = async { foursquarePlacesDao.getPlaces() }.await()
        return@runBlocking result as List<FoursquarePlace>
    }

    suspend fun addPlacesToDB(places: List<FoursquarePlace>) {
        withContext(Dispatchers.IO) {
            foursquarePlacesDao.insertPlaces(places)
        }
    }

    suspend fun deletePlacesFromDB() {
        withContext(Dispatchers.IO) { foursquarePlacesDao.deletePlaces() }
    }
    //endregion

}