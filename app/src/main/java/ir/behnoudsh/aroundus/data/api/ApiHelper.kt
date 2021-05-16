package ir.behnoudsh.aroundus.data.api

import ir.behnoudsh.aroundus.di.component.ApiComponent
import ir.behnoudsh.aroundus.di.component.DaggerApiComponent
import javax.inject.Inject

class ApiHelper {

    @Inject
    lateinit var apiService: ApiService

    init {
        val apiComponent: ApiComponent = DaggerApiComponent.create()
        apiComponent.inject(this)
    }

    fun getPlaces(lng_lat: String, offset: Int) = apiService.getPlaces(lng_lat, offset)

    fun getPlaceDetails(venueId: String) = apiService.getPlaceDetails(venueId)

}