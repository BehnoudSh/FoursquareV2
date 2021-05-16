package ir.behnoudsh.aroundus.data.repository

import io.reactivex.Single
import ir.behnoudsh.aroundus.data.api.ApiHelper
import ir.behnoudsh.aroundus.data.model.venue.ResponseVenue
import ir.behnoudsh.aroundus.data.model.venues.ResponseVenues
import ir.behnoudsh.aroundus.di.component.ApiHelperComponent
import ir.behnoudsh.aroundus.di.component.DaggerApiHelperComponent

import javax.inject.Inject

class PlacesRepository {

    @Inject
    lateinit var apiHelper: ApiHelper

    init {
        val apiHelperComponent: ApiHelperComponent = DaggerApiHelperComponent.create();
        apiHelperComponent.inject(this)
    }

    fun getPlaces(lng_lat: String, offset: Int): Single<ResponseVenues>? {

        return apiHelper.getPlaces(lng_lat, offset)

    }

    fun getPlaceDetails(venueId: String): Single<ResponseVenue>? {

        return apiHelper.getPlaceDetails(venueId)

    }
}